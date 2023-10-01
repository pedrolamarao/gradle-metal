package br.dev.pedrolamarao.gradle.metal.cxx;

import br.dev.pedrolamarao.gradle.metal.base.Metal;
import br.dev.pedrolamarao.gradle.metal.base.MetalBasePlugin;
import br.dev.pedrolamarao.gradle.metal.base.MetalExtension;
import br.dev.pedrolamarao.gradle.metal.cpp.MetalCppPlugin;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.Directory;

/**
 * C++ language support plugin.
 */
public class MetalCxxPlugin implements Plugin<Project>
{
    /**
     * {@inheritDoc}
     */
    @Override
    public void apply (Project project)
    {
        project.getPluginManager().apply(MetalBasePlugin.class);
        project.getPluginManager().apply(MetalCppPlugin.class);

        final var metal = project.getExtensions().getByType(MetalExtension.class);

        final var cxx = project.getObjects().domainObjectContainer(MetalCxxSources.class, name -> createSources(project,name));
        metal.getExtensions().add("cxx", cxx);
    }

    static MetalCxxSources createSources (Project project, String name)
    {
        final var configurations = project.getConfigurations();
        final var layout = project.getLayout();
        final var metal = project.getExtensions().findByType(MetalExtension.class);
        final var objects = project.getObjects();
        final var tasks = project.getTasks();

        final var commandsDirectory = layout.getBuildDirectory().dir("db/%s/cxx".formatted(name));
        final var compileOptions = objects.listProperty(String.class).convention(metal.getCompileOptions());
        final var includables = configurations.named(Metal.INCLUDABLE_DEPENDENCIES);
        final var importables = configurations.named(Metal.IMPORTABLE_DEPENDENCIES);
        final var sources = objects.sourceDirectorySet(name,name);
        sources.srcDir(layout.getProjectDirectory().dir("src/%s/cxx".formatted(name)));
        final var objectDirectory = layout.getBuildDirectory().dir("obj/%s/cxx".formatted(name));

        final var commandsTask = tasks.register("commands-%s-cxx".formatted(name), MetalCxxCommandsTask.class, task ->
        {
            task.getCompileOptions().set(compileOptions);
            task.getImportables().from(importables);
            task.getIncludables().from(includables);
            task.getObjectDirectory().set(objectDirectory.map(Directory::getAsFile));
            task.getOutputDirectory().set(commandsDirectory);
            task.setSource(sources);
        });
        configurations.named(Metal.COMMANDS_ELEMENTS).configure(it -> it.getOutgoing().artifact(commandsTask));

        final var compileTask = tasks.register("compile-%s-cxx".formatted(name), MetalCxxCompileTask.class, task ->
        {
            task.getCompileOptions().set(compileOptions);
            task.getImportables().from(importables);
            task.getIncludables().from(includables);
            task.getOutputDirectory().set(objectDirectory);
            task.setSource(sources);
        });
        tasks.named("compile").configure(it -> it.dependsOn(compileTask));

        return new MetalCxxSources(commandsTask, compileOptions, compileTask, name);
    }
}
