package br.dev.pedrolamarao.gradle.metal.ixx;

import br.dev.pedrolamarao.gradle.metal.base.MetalBasePlugin;
import br.dev.pedrolamarao.gradle.metal.base.MetalExtension;
import br.dev.pedrolamarao.gradle.metal.cpp.MetalCppPlugin;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.Directory;

import static br.dev.pedrolamarao.gradle.metal.base.MetalBasePlugin.*;

public class MetalIxxPlugin implements Plugin<Project>
{
    @Override
    public void apply (Project project)
    {
        project.getPluginManager().apply(MetalBasePlugin.class);
        project.getPluginManager().apply(MetalCppPlugin.class);

        final var metal = project.getExtensions().getByType(MetalExtension.class);

        final var ixx = project.getObjects().domainObjectContainer(MetalIxxSources.class, name -> createSources(project,name));
        metal.getExtensions().add("ixx", ixx);
    }

    static MetalIxxSources createSources (Project project, String name)
    {
        final var configurations = project.getConfigurations();
        final var layout = project.getLayout();
        final var objects = project.getObjects();
        final var tasks = project.getTasks();

        final var commandsDirectory = layout.getBuildDirectory().dir("db/%s/ixx".formatted(name));
        final var compileOptions = objects.listProperty(String.class);
        final var importables = configurations.named(IMPORTABLE_DEPENDENCIES);
        final var includables = configurations.named(INCLUDABLE_DEPENDENCIES);
        final var sources = objects.sourceDirectorySet(name,name);
        sources.srcDir(layout.getProjectDirectory().dir("src/%s/ixx".formatted(name)));
        final var objectDirectory = layout.getBuildDirectory().dir("bmi/%s/ixx".formatted(name));

        final var commandsTask = tasks.register("commands-%s-ixx".formatted(name), MetalIxxCommandsTask.class, task ->
        {
            task.getCompileOptions().set(compileOptions);
            task.getIncludables().from(includables);
            task.getImportables().from(importables);
            task.getObjectDirectory().set(objectDirectory.map(Directory::getAsFile));
            task.getOutputDirectory().set(commandsDirectory);
            task.setSource(sources);
        });
        configurations.named(COMMANDS_ELEMENTS).configure(it -> it.getOutgoing().artifact(commandsTask));

        final var compileTask = tasks.register("compile-%s-ixx".formatted(name), MetalIxxCompileTask.class, task ->
        {
            task.getCompileOptions().set(compileOptions);
            task.getIncludables().from(includables);
            task.getImportables().from(importables);
            task.getOutputDirectory().set(objectDirectory);
            task.setSource(sources);
        });
        configurations.named(MetalBasePlugin.IMPORTABLE_ELEMENTS).configure(configuration -> {
            configuration.getOutgoing().artifact(compileTask.map(MetalIxxCompileTask::getTargetDirectory), it -> it.builtBy(compileTask));
        });

        return new MetalIxxSources(compileOptions, compileTask, name);
    }
}
