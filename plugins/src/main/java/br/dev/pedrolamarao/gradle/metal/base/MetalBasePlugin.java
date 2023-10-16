package br.dev.pedrolamarao.gradle.metal.base;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.Exec;
import org.gradle.language.base.plugins.LifecycleBasePlugin;

/**
 * Metal projects and tasks.
 */
public class MetalBasePlugin implements Plugin<Project>
{
    /**
     * {@inheritDoc}
     */
    @Override
    public void apply (Project project)
    {
        project.getPluginManager().apply(LifecycleBasePlugin.class);

        project.getGradle().getSharedServices().registerIfAbsent("metal",MetalService.class, it -> {});

        final var configurations = project.getConfigurations();
        final var tasks = project.getTasks();

        // dependency scopes

        final var api = configurations.dependencyScope("api", configuration -> {
            configuration.setDescription("metal api dependencies");
        });

        final var implementation = configurations.dependencyScope("implementation", configuration -> {
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

        configurations.resolvable(Metal.COMMANDS_DEPENDENCIES, configuration -> {
            configuration.attributes(it -> {
                it.attribute(MetalCapability.ATTRIBUTE, MetalCapability.COMMANDS);
            });
            configuration.setDescription("metal commands database dependencies");
        });

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

        // extensions

        final var metal = project.getExtensions().create("metal", MetalExtension.class);

        final var applications = project.getObjects().domainObjectContainer(MetalApplication.class, name -> createApplication(project,name));
        metal.getExtensions().add("applications", applications);

        final var archives = project.getObjects().domainObjectContainer(MetalArchive.class, name -> createArchive(project,name));
        metal.getExtensions().add("archives", archives);

        // tasks

        tasks.register("compile",task ->
        {
            task.setGroup("metal");
            task.setDescription("compile source files");
        });

        final var archive = tasks.register("archive",task ->
        {
            task.setGroup("metal");
            task.setDescription("assemble archives");
        });

        final var link = tasks.register("link",task ->
        {
            task.setGroup("metal");
            task.setDescription("assemble executables");
        });

        final var test = tasks.register("test",task ->
        {
            task.setGroup("metal");
            task.setDescription("run test executables");
        });

        tasks.named("assemble").configure(it -> it.dependsOn(archive,link));

        tasks.named("check").configure(it -> it.dependsOn(test));
    }

    static MetalApplication createApplication (Project project, String name)
    {
        final var configurations = project.getConfigurations();
        final var layout = project.getLayout();
        final var metal = project.getExtensions().getByType(MetalExtension.class);
        final var objects = project.getObjects();
        final var tasks = project.getTasks();

        final var linkTask = tasks.register("link-%s".formatted(name),MetalLinkTask.class);
        final var component = objects.newInstance(MetalApplication.class,linkTask,name);
        component.getLinkOptions().convention(metal.getLinkOptions());
        component.getTargets().convention(metal.getTargets());
        component.getArchives().from(configurations.named(Metal.LINKABLE_DEPENDENCIES));

        linkTask.configure(task ->
        {
            task.onlyIf(it -> component.getTargets().zip(task.getTarget(),(targets,target) -> targets.isEmpty() || targets.contains(target)).get());
            task.getArchives().from(component.getArchives());
            task.getLinkOptions().convention(component.getLinkOptions());
            task.getOutputDirectory().convention(layout.getBuildDirectory().dir("exe/%s".formatted(name)));
            task.setSource(component.getSources());
            task.getTarget().convention(metal.getTarget());
        });

        tasks.register("run-%s".formatted(name), Exec.class, task ->
        {
            task.onlyIf(it -> {
                if (! linkTask.get().getOutput().get().getAsFile().exists()) return false;
                if (! component.getTargets().get().isEmpty()) return false;
                return component.getTargets().get().contains(metal.getTarget().get());
            });
            task.dependsOn(linkTask);
            task.executable(linkTask.flatMap(MetalLinkTask::getOutput).get());
        });

        configurations.named(Metal.EXECUTABLE_ELEMENTS).configure(it -> it.getOutgoing().artifact(linkTask));

        tasks.named("link").configure(it -> it.dependsOn(linkTask));

        return component;
    }

    static MetalArchive createArchive (Project project, String name)
    {
        final var configurations = project.getConfigurations();
        final var layout = project.getLayout();
        final var metal = project.getExtensions().getByType(MetalExtension.class);
        final var objects = project.getObjects();
        final var tasks = project.getTasks();

        final var archiveTask = tasks.register("archive-%s".formatted(name),MetalArchiveTask.class);
        final var component = objects.newInstance(MetalArchive.class,archiveTask,name);
        component.getArchiveOptions().convention(metal.getArchiveOptions());
        component.getTargets().convention(metal.getTargets());

        archiveTask.configure(task ->
        {
            task.onlyIf(it -> component.getTargets().zip(task.getTarget(),(targets,target) -> targets.isEmpty() || targets.contains(target)).get());
            task.getArchiveOptions().convention(component.getArchiveOptions());
            task.getOutputDirectory().convention(layout.getBuildDirectory().dir("lib/%s".formatted(name)));
            task.setSource(component.getSources());
            task.getTarget().convention(metal.getTarget());
        });

        configurations.named(Metal.LINKABLE_ELEMENTS).configure(it -> it.getOutgoing().artifact(archiveTask));

        tasks.named("archive").configure(it -> it.dependsOn(archiveTask));

        return component;
    }
}
