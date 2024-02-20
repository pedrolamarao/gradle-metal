// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
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

        final var api = configurations.dependencyScope("api", configuration -> {
            configuration.setDescription("library api dependencies");
        });

        final var implementation = configurations.dependencyScope("implementation", configuration -> {
            configuration.setDescription("library implementation dependencies");
            configuration.extendsFrom(api.get());
        });

        final var commandsScope = configurations.dependencyScope("commands");

        configurations.consumable(Metal.COMMANDS_ELEMENTS, configuration -> {
            configuration.attributes(it -> {
                it.attribute(MetalCapability.ATTRIBUTE, MetalCapability.COMMANDS);
                it.attribute(MetalVisibility.ATTRIBUTE, MetalVisibility.COMPILE);
            });
            configuration.extendsFrom(implementation.get());
            configuration.setDescription("project commands elements");
        });

        final var commandsDependencies = configurations.resolvable(Metal.COMMANDS_DEPENDENCIES, configuration -> {
            configuration.attributes(it -> {
                it.attribute(MetalCapability.ATTRIBUTE, MetalCapability.COMMANDS);
                it.attribute(MetalVisibility.ATTRIBUTE, MetalVisibility.COMPILE);
            });
            configuration.extendsFrom(commandsScope.get());
            configuration.setDescription("project commands dependencies");
        });

        final var inputFiles = project.files(commandsDependencies);
        final var outputFile = project.file("compile_commands.json");

        project.getTasks().register("aggregateCommands",SourceTask.class).configure(task ->
        {
            task.dependsOn(commandsDependencies.map(Configuration::getBuildDependencies));
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
