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
        project.getExtensions().getByType(MetalExtension.class).getExtensions().add("applications", applications);
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
}
