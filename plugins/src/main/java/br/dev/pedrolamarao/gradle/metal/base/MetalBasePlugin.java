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

        configurations.create(COMMANDS_ELEMENTS, configuration ->
        {
            configuration.setCanBeConsumed(true);
            configuration.setCanBeResolved(false);
            configuration.attributes(it -> it.attribute(NativeCapability.ATTRIBUTE,NativeCapability.COMMANDS));
        });

        final var nativeImplementation = configurations.create("nativeImplementation",configuration -> {
            configuration.setCanBeConsumed(true);
            configuration.setCanBeResolved(false);
        });

        configurations.create(EMPTY_ELEMENTS, configuration -> {
            configuration.attributes(it -> it.attribute(NativeCapability.ATTRIBUTE,NativeCapability.NONE));
            configuration.setCanBeConsumed(true);
            configuration.setCanBeDeclared(false);
            configuration.setCanBeResolved(false);
            configuration.setVisible(false);
        });

        configurations.create(LINKABLE_DEPENDENCIES, configuration -> {
            configuration.setCanBeConsumed(false);
            configuration.setCanBeResolved(true);
            configuration.attributes(it -> it.attribute(NativeCapability.ATTRIBUTE,NativeCapability.LINKABLE));
            configuration.extendsFrom(nativeImplementation);
        });

        configurations.create(LINKABLE_ELEMENTS, configuration -> {
            configuration.setCanBeConsumed(true);
            configuration.setCanBeResolved(false);
            configuration.attributes(it -> it.attribute(NativeCapability.ATTRIBUTE,NativeCapability.LINKABLE));
        });

        project.getDependencies().getAttributesSchema().attribute(NativeCapability.ATTRIBUTE, it -> {
            it.getCompatibilityRules().add(NativeCapabilityCompatibilityRule.class);
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

        final var linkTask = tasks.register("link-%s".formatted(name), NativeLinkTask.class, it ->
        {
            final var output = project.getLayout().getBuildDirectory().file("exe/%s/%s.exe".formatted(name,project.getName()));
            it.getLibraryDependencies().from(configurations.named(LINKABLE_DEPENDENCIES));
            it.getOptions().convention(linkOptions);
            it.getOutput().set(output);
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

        final var archiveTask = tasks.register("archive-%s".formatted(name), NativeArchiveTask.class, it ->
        {
            final var output = project.getLayout().getBuildDirectory().file("lib/%s/%s.lib".formatted(name,project.getName()));
            it.getOptions().convention(archiveOptions);
            it.getOutput().set(output);
        });
        configurations.named(LINKABLE_ELEMENTS).configure(it -> it.getOutgoing().artifact(archiveTask));
        tasks.named("archive").configure(it -> it.dependsOn(archiveTask));

        return new MetalArchive(archiveOptions, archiveTask, name);
    }
}
