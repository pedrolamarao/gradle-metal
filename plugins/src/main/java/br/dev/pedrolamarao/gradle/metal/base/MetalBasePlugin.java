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
        project.getPluginManager().apply(NativeBasePlugin.class);

        final var applications = project.getObjects().domainObjectContainer(MetalApplication.class, name -> createApplication(project,name));
        final var archives = project.getObjects().domainObjectContainer(MetalArchive.class, name -> createArchive(project,name));
        project.getExtensions().getByType(MetalExtension.class).getExtensions().add("applications", applications);
        project.getExtensions().getByType(MetalExtension.class).getExtensions().add("archives", archives);
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
            it.getLibraryDependencies().from(configurations.named("nativeLinkDependencies"));
            it.getOptions().convention(linkOptions);
            it.getOutput().set(output);
        });

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

        configurations.named("nativeLinkElements").configure(it -> {
            it.getOutgoing().artifact(archiveTask);
        });

        return new MetalArchive(archiveOptions, archiveTask, name);
    }
}
