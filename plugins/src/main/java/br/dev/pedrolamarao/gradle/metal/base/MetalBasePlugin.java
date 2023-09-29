package br.dev.pedrolamarao.gradle.metal.base;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.language.base.plugins.LifecycleBasePlugin;

public class MetalBasePlugin implements Plugin<Project>
{
    public static final String COMMANDS_ELEMENTS = "metalCommandsElements";

    public static final String COMMANDS_DEPENDENCIES = "commands";

    public static final String EMPTY_ELEMENTS = "metalEmptyElements";

    public static final String IMPORTABLE_ELEMENTS = "metalImportableElements";

    public static final String IMPORTABLE_DEPENDENCIES = "metalImportableDependencies";

    public static final String INCLUDABLE_ELEMENTS = "metalIncludableElements";

    public static final String INCLUDABLE_DEPENDENCIES = "metalIncludableDependencies";

    public static final String LINKABLE_DEPENDENCIES = "metalLinkableDependencies";

    public static final String LINKABLE_ELEMENTS = "metalLinkableElements";

    @Override
    public void apply (Project project)
    {
        project.getPluginManager().apply(LifecycleBasePlugin.class);

        final var configurations = project.getConfigurations();
        final var tasks = project.getTasks();

        configurations.consumable(COMMANDS_ELEMENTS, configuration -> {
            configuration.attributes(it -> it.attribute(MetalCapability.ATTRIBUTE, MetalCapability.COMMANDS));
        });

        final var nativeImplementation = configurations.dependencyScope("nativeImplementation");

        configurations.consumable(EMPTY_ELEMENTS, configuration -> {
            configuration.attributes(it -> it.attribute(MetalCapability.ATTRIBUTE, MetalCapability.NONE));
            configuration.setVisible(false);
        });

        project.getConfigurations().resolvable(IMPORTABLE_DEPENDENCIES, configuration -> {
            configuration.attributes(it -> it.attribute(MetalCapability.ATTRIBUTE, MetalCapability.IMPORTABLE));
            configuration.extendsFrom(nativeImplementation.get());
        });

        project.getConfigurations().consumable(MetalBasePlugin.IMPORTABLE_ELEMENTS, configuration -> {
            configuration.attributes(it -> it.attribute(MetalCapability.ATTRIBUTE, MetalCapability.IMPORTABLE));
        });

        project.getConfigurations().resolvable(MetalBasePlugin.INCLUDABLE_DEPENDENCIES, configuration -> {
            configuration.attributes(it -> it.attribute(MetalCapability.ATTRIBUTE, MetalCapability.INCLUDABLE));
            configuration.extendsFrom(nativeImplementation.get());
        });

        project.getConfigurations().consumable(MetalBasePlugin.INCLUDABLE_ELEMENTS, configuration -> {
            configuration.attributes(it -> it.attribute(MetalCapability.ATTRIBUTE, MetalCapability.INCLUDABLE));
        });

        configurations.resolvable(LINKABLE_DEPENDENCIES, configuration -> {
            configuration.attributes(it -> it.attribute(MetalCapability.ATTRIBUTE, MetalCapability.LINKABLE));
            configuration.extendsFrom(nativeImplementation.get());
        });

        configurations.consumable(LINKABLE_ELEMENTS, configuration -> {
            configuration.attributes(it -> it.attribute(MetalCapability.ATTRIBUTE, MetalCapability.LINKABLE));
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
            it.getLinkables().from(configurations.named(LINKABLE_DEPENDENCIES));
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
        configurations.named(LINKABLE_ELEMENTS).configure(it -> it.getOutgoing().artifact(archiveTask));
        tasks.named("archive").configure(it -> it.dependsOn(archiveTask));

        return new MetalArchive(archiveOptions, archiveTask, name);
    }
}
