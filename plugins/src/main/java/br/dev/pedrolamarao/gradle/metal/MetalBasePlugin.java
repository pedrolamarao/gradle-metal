// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.SourceTask;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * Gradle Metal base plugin.
 */
public class MetalBasePlugin implements Plugin<Project>
{
    @Override
    public void apply (Project project)
    {
        final var configurations = project.getConfigurations();

        project.getGradle().getSharedServices().registerIfAbsent("metal",MetalService.class,it -> {});

        project.getExtensions().create("metal",MetalExtension.class);

        // dependency management

        final var api = configurations.dependencyScope("api", configuration -> {
            configuration.setDescription("api dependencies");
        });

        final var implementation = configurations.dependencyScope("implementation", configuration -> {
            configuration.setDescription("implementation dependencies");
            configuration.extendsFrom(api.get());
        });

        final var testImplementation = configurations.dependencyScope("testImplementation", configuration -> {
            configuration.setDescription("test implementation dependencies");
            configuration.extendsFrom(implementation.get());
        });

        final var commands = configurations.dependencyScope("commands");

        // outgoing elements

        configurations.consumable(Metal.COMMANDS_ELEMENTS, configuration -> {
            configuration.attributes(it -> {
                it.attribute(MetalCapability.ATTRIBUTE, MetalCapability.COMMANDS);
                it.attribute(MetalVisibility.ATTRIBUTE, MetalVisibility.COMPILE);
            });
            configuration.setDescription("commands elements");
        });

        configurations.consumable(Metal.IMPORTABLE_ELEMENTS, configuration -> {
            configuration.attributes(it -> {
                it.attribute(MetalCapability.ATTRIBUTE, MetalCapability.IMPORTABLE);
                it.attribute(MetalVisibility.ATTRIBUTE, MetalVisibility.COMPILE);
            });
            configuration.extendsFrom(api.get());
            configuration.setDescription("include dependencies");
        });

        configurations.consumable(Metal.INCLUDABLE_ELEMENTS, configuration -> {
            configuration.attributes(it -> {
                it.attribute(MetalCapability.ATTRIBUTE, MetalCapability.INCLUDABLE);
                it.attribute(MetalVisibility.ATTRIBUTE, MetalVisibility.COMPILE);
            });
            configuration.extendsFrom(api.get());
            configuration.setDescription("includable elements");
        });

        configurations.consumable(Metal.LINKABLE_ELEMENTS, configuration -> {
            configuration.attributes(it -> {
                it.attribute(MetalCapability.ATTRIBUTE, MetalCapability.LINKABLE);
                it.attribute(MetalVisibility.ATTRIBUTE, MetalVisibility.COMPILE);
            });
            configuration.extendsFrom(api.get());
            configuration.setDescription("linkable elements");
        });

        // incoming dependencies

        final var commandsDependencies = configurations.resolvable(Metal.COMMANDS_DEPENDENCIES, configuration -> {
            configuration.attributes(it -> {
                it.attribute(MetalCapability.ATTRIBUTE, MetalCapability.COMMANDS);
                it.attribute(MetalVisibility.ATTRIBUTE, MetalVisibility.COMPILE);
            });
            configuration.extendsFrom(commands.get());
            configuration.setDescription("commands dependencies");
        });

        configurations.resolvable(Metal.IMPORTABLE_DEPENDENCIES, configuration -> {
            configuration.attributes(it -> {
                it.attribute(MetalCapability.ATTRIBUTE, MetalCapability.IMPORTABLE);
                it.attribute(MetalVisibility.ATTRIBUTE, MetalVisibility.COMPILE);
            });
            configuration.extendsFrom(implementation.get());
            configuration.setDescription("import dependencies");
        });

        configurations.resolvable(Metal.INCLUDABLE_DEPENDENCIES, configuration -> {
            configuration.attributes(it -> {
                it.attribute(MetalCapability.ATTRIBUTE, MetalCapability.INCLUDABLE);
                it.attribute(MetalVisibility.ATTRIBUTE, MetalVisibility.COMPILE);
            });
            configuration.extendsFrom(implementation.get());
            configuration.setDescription("include dependencies");
        });

        configurations.resolvable(Metal.LINKABLE_DEPENDENCIES, configuration -> {
            configuration.attributes(it -> {
                it.attribute(MetalCapability.ATTRIBUTE, MetalCapability.LINKABLE);
                it.attribute(MetalVisibility.ATTRIBUTE, MetalVisibility.COMPILE);
            });
            configuration.extendsFrom(implementation.get());
            configuration.setDescription("link dependencies");
        });

        configurations.resolvable("testImportDependencies", configuration -> {
            configuration.attributes(it -> {
                it.attribute(MetalCapability.ATTRIBUTE, MetalCapability.IMPORTABLE);
                it.attribute(MetalVisibility.ATTRIBUTE, MetalVisibility.COMPILE);
            });
            configuration.extendsFrom(testImplementation.get());
            configuration.setDescription("test import dependencies");
        });

        configurations.resolvable("testIncludeDependencies", configuration -> {
            configuration.attributes(it -> {
                it.attribute(MetalCapability.ATTRIBUTE, MetalCapability.INCLUDABLE);
                it.attribute(MetalVisibility.ATTRIBUTE, MetalVisibility.COMPILE);
            });
            configuration.extendsFrom(testImplementation.get());
            configuration.setDescription("test include dependencies");
        });

        configurations.resolvable("testLinkDependencies", configuration -> {
            configuration.attributes(it -> {
                it.attribute(MetalCapability.ATTRIBUTE, MetalCapability.LINKABLE);
                it.attribute(MetalVisibility.ATTRIBUTE, MetalVisibility.COMPILE);
            });
            configuration.extendsFrom(testImplementation.get());
            configuration.setDescription("test link dependencies");
        });

        // aggregate commands task

        project.getTasks().register("aggregateCommands",SourceTask.class).configure(task ->
        {
            final var inputFiles = task.getProject().getObjects().fileCollection();
            inputFiles.from(commandsDependencies);
            final var application = task.getProject().getExtensions().findByType(MetalApplication.class);
            if (application != null) inputFiles.from(application.getCommandFiles());
            final var library = task.getProject().getExtensions().findByType(MetalLibrary.class);
            if (library != null) inputFiles.from(library.getCommandFiles());

            final var outputFile = task.getProject().file("compile_commands.json");

            task.setGroup("metal");
            task.getOutputs().file(outputFile);
            task.setSource(inputFiles);

            task.doLast(ignore ->
            {
                final var list = new ArrayList<>();
                task.getSource().forEach(file -> {
                    final var parsed = (List<?>) new groovy.json.JsonSlurper().parse(file);
                    list.addAll(parsed);
                });

                final var builder = new groovy.json.JsonBuilder();
                builder.call(list);

                try (var writer = Files.newBufferedWriter(outputFile.toPath())) {
                    writer.write(builder.toPrettyString());
                }
                catch (IOException e) { throw new RuntimeException(e); }
            });
        });
    }
}
