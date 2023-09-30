package br.dev.pedrolamarao.gradle.metal.c;

import br.dev.pedrolamarao.gradle.metal.base.MetalBasePlugin;
import br.dev.pedrolamarao.gradle.metal.base.MetalExtension;
import br.dev.pedrolamarao.gradle.metal.cpp.MetalCppPlugin;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.Directory;

import static br.dev.pedrolamarao.gradle.metal.base.Metal.COMMANDS_ELEMENTS;
import static br.dev.pedrolamarao.gradle.metal.base.Metal.INCLUDABLE_DEPENDENCIES;

public class MetalCPlugin implements Plugin<Project>
{
    @Override
    public void apply (Project project)
    {
        project.getPluginManager().apply(MetalBasePlugin.class);
        project.getPluginManager().apply(MetalCppPlugin.class);

        final var metal = project.getExtensions().getByType(MetalExtension.class);

        final var c = project.getObjects().domainObjectContainer(MetalCSources.class, name -> createSources(project,name));
        metal.getExtensions().add("c", c);
    }

    static MetalCSources createSources (Project project, String name)
    {
        final var configurations = project.getConfigurations();
        final var layout = project.getLayout();
        final var metal = project.getExtensions().findByType(MetalExtension.class);
        final var objects = project.getObjects();
        final var tasks = project.getTasks();

        final var commandsDirectory = layout.getBuildDirectory().dir("db/%s/c".formatted(name));
        final var compileOptions = objects.listProperty(String.class).convention(metal.getCompileOptions());
        final var includables = configurations.named(INCLUDABLE_DEPENDENCIES);
        final var sources = objects.sourceDirectorySet(name,name);
        sources.srcDir(layout.getProjectDirectory().dir("src/%s/c".formatted(name)));
        final var objectDirectory = layout.getBuildDirectory().dir("obj/%s/c".formatted(name));

        final var commandsTask = tasks.register("commands-%s-c".formatted(name), MetalCCommandsTask.class, task ->
        {
            task.getCompileOptions().set(compileOptions);
            task.getIncludables().from(includables);
            task.getObjectDirectory().set(objectDirectory.map(Directory::getAsFile));
            task.getOutputDirectory().set(commandsDirectory);
            task.setSource(sources);
        });
        configurations.named(COMMANDS_ELEMENTS).configure(it -> it.getOutgoing().artifact(commandsTask));

        final var compileTask = tasks.register("compile-%s-c".formatted(name), MetalCCompileTask.class, task ->
        {
            task.getCompileOptions().set(compileOptions);
            task.getIncludables().from(includables);
            task.getOutputDirectory().set(objectDirectory);
            task.setSource(sources);
        });
        tasks.named("compile").configure(it -> it.dependsOn(compileTask));

        return new MetalCSources(commandsTask, compileOptions, compileTask, name);
    }
}
