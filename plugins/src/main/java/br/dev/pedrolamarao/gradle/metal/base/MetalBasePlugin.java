package br.dev.pedrolamarao.gradle.metal.base;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.language.base.plugins.LifecycleBasePlugin;

public class MetalBasePlugin implements Plugin<Project>
{
    @Override
    public void apply (Project project)
    {
        project.getPluginManager().apply(LifecycleBasePlugin.class);

        final var configurations = project.getConfigurations();
        final var tasks = project.getTasks();

        // dependency scopes

        final var api = configurations.dependencyScope("metalApi", configuration -> {
            configuration.setDescription("metal api dependencies");
        });

        final var implementation = configurations.dependencyScope("metalImplementation", configuration -> {
            configuration.setDescription("metal implementation dependencies");
            configuration.extendsFrom(api.get());
        });

        // outgoing configurations

        configurations.consumable(Metal.COMMANDS_ELEMENTS, configuration -> {
            configuration.attributes(it -> {
                it.attribute(MetalCapability.ATTRIBUTE, MetalCapability.COMMANDS);
            });
            configuration.setDescription("metal commands database elements");
        });

        configurations.consumable(Metal.EMPTY_ELEMENTS, configuration -> {
            configuration.attributes(it -> {
                it.attribute(MetalCapability.ATTRIBUTE, MetalCapability.NONE);
            });
            configuration.setDescription("metal empty elements (can we remove this?)");
        });

        project.getConfigurations().consumable(Metal.EXECUTABLE_ELEMENTS, configuration -> {
            configuration.attributes(it -> {
                it.attribute(MetalCapability.ATTRIBUTE, MetalCapability.EXECUTABLE);
                it.attribute(MetalVisibility.ATTRIBUTE, MetalVisibility.RUN);
            });
            configuration.extendsFrom(implementation.get());
            configuration.setDescription("metal executable elements");
        });

        project.getConfigurations().consumable(Metal.IMPORTABLE_ELEMENTS, configuration -> {
            configuration.attributes(it -> {
                it.attribute(MetalCapability.ATTRIBUTE, MetalCapability.IMPORTABLE);
                it.attribute(MetalVisibility.ATTRIBUTE, MetalVisibility.COMPILE);
            });
            configuration.extendsFrom(api.get());
            configuration.setDescription("metal importable elements");
        });

        project.getConfigurations().consumable(Metal.INCLUDABLE_ELEMENTS, configuration -> {
            configuration.attributes(it -> {
                it.attribute(MetalCapability.ATTRIBUTE, MetalCapability.INCLUDABLE);
                it.attribute(MetalVisibility.ATTRIBUTE, MetalVisibility.COMPILE);
            });
            configuration.extendsFrom(api.get());
            configuration.setDescription("metal includable elements");
        });

        configurations.consumable(Metal.LINKABLE_ELEMENTS, configuration -> {
            configuration.attributes(it -> {
                it.attribute(MetalCapability.ATTRIBUTE, MetalCapability.LINKABLE);
                it.attribute(MetalVisibility.ATTRIBUTE, MetalVisibility.COMPILE);
            });
            configuration.extendsFrom(api.get());
            configuration.setDescription("metal linkable elements");
        });

        // incoming configurations

        configurations.resolvable(Metal.EXECUTABLE_DEPENDENCIES, configuration -> {
            configuration.attributes(it -> {
                it.attribute(MetalCapability.ATTRIBUTE, MetalCapability.EXECUTABLE);
                it.attribute(MetalVisibility.ATTRIBUTE, MetalVisibility.RUN);
            });
            configuration.extendsFrom(implementation.get());
            configuration.setDescription("metal executable dependencies");
        });

        project.getConfigurations().resolvable(Metal.IMPORTABLE_DEPENDENCIES, configuration -> {
            configuration.attributes(it -> {
                it.attribute(MetalCapability.ATTRIBUTE, MetalCapability.IMPORTABLE);
                it.attribute(MetalVisibility.ATTRIBUTE, MetalVisibility.COMPILE);
            });
            configuration.extendsFrom(implementation.get());
            configuration.setDescription("metal importable dependencies");
        });

        project.getConfigurations().resolvable(Metal.INCLUDABLE_DEPENDENCIES, configuration -> {
            configuration.attributes(it -> {
                it.attribute(MetalCapability.ATTRIBUTE, MetalCapability.INCLUDABLE);
                it.attribute(MetalVisibility.ATTRIBUTE, MetalVisibility.COMPILE);
            });
            configuration.extendsFrom(implementation.get());
            configuration.setDescription("metal includable dependencies");
        });

        configurations.resolvable(Metal.LINKABLE_DEPENDENCIES, configuration -> {
            configuration.attributes(it -> {
                it.attribute(MetalCapability.ATTRIBUTE, MetalCapability.LINKABLE);
                it.attribute(MetalVisibility.ATTRIBUTE, MetalVisibility.COMPILE);
            });
            configuration.extendsFrom(implementation.get());
            configuration.setDescription("metal linkable dependencies");
        });

        project.getDependencies().getAttributesSchema().attribute(MetalCapability.ATTRIBUTE, it -> {
            it.getCompatibilityRules().add(MetalCapabilityCompatibilityRule.class);
        });

        final var metal = project.getExtensions().create("metal", MetalExtension.class);

        final var applications = project.getObjects().domainObjectContainer(MetalApplication.class, name -> createApplication(project,name));
        metal.getExtensions().add("applications", applications);

        final var archives = project.getObjects().domainObjectContainer(MetalArchive.class, name -> createArchive(project,name));
        metal.getExtensions().add("archives", archives);

        tasks.register("compile").configure(task ->
        {
            task.setGroup("metal");
            task.setDescription("compile source files");
        });

        tasks.register("archive").configure(task ->
        {
            task.setGroup("metal");
            task.setDescription("assemble archives");
        });

        tasks.register("link").configure(task ->
        {
            task.setGroup("metal");
            task.setDescription("assemble executables");
        });
    }

    static MetalApplication createApplication (Project project, String name)
    {
        final var configurations = project.getConfigurations();
        final var objects = project.getObjects();
        final var tasks = project.getTasks();

        final var linkOptions = objects.listProperty(String.class);
        final var outputDirectory = project.getLayout().getBuildDirectory().dir("exe/%s".formatted(name));

        final var linkTask = tasks.register("link-%s".formatted(name), MetalLinkTask.class, it ->
        {
            it.getLinkables().from(configurations.named(Metal.LINKABLE_DEPENDENCIES));
            it.getLinkOptions().convention(linkOptions);
            it.getOutputDirectory().set(outputDirectory);
        });
        tasks.named("link").configure(it -> it.dependsOn(linkTask));

        return new MetalApplication(linkOptions, linkTask, name);
    }

    static MetalArchive createArchive (Project project, String name)
    {
        final var configurations = project.getConfigurations();
        final var objects = project.getObjects();
        final var tasks = project.getTasks();

        final var archiveOptions = objects.listProperty(String.class);
        final var outputDirectory = project.getLayout().getBuildDirectory().dir("lib/%s".formatted(name));

        final var archiveTask = tasks.register("archive-%s".formatted(name), MetalArchiveTask.class, it ->
        {
            it.getArchiveOptions().convention(archiveOptions);
            it.getOutputDirectory().set(outputDirectory);
        });
        configurations.named(Metal.LINKABLE_ELEMENTS).configure(it -> it.getOutgoing().artifact(archiveTask));
        tasks.named("archive").configure(it -> it.dependsOn(archiveTask));

        return new MetalArchive(archiveOptions, archiveTask, name);
    }
}
