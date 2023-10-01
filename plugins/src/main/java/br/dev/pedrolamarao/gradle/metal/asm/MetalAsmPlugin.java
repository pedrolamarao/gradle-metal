package br.dev.pedrolamarao.gradle.metal.asm;

import br.dev.pedrolamarao.gradle.metal.base.MetalBasePlugin;
import br.dev.pedrolamarao.gradle.metal.base.MetalExtension;
import br.dev.pedrolamarao.gradle.metal.cpp.MetalCppPlugin;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.Directory;

import static br.dev.pedrolamarao.gradle.metal.base.Metal.COMMANDS_ELEMENTS;
import static br.dev.pedrolamarao.gradle.metal.base.Metal.INCLUDABLE_DEPENDENCIES;

/**
 * Assembler language support plugin.
 */
public class MetalAsmPlugin implements Plugin<Project>
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

        final var asm = project.getObjects().domainObjectContainer(MetalAsmSources.class, name -> createSources(project,name));
        metal.getExtensions().add("asm", asm);
    }

    static MetalAsmSources createSources (Project project, String name)
    {
        final var configurations = project.getConfigurations();
        final var layout = project.getLayout();
        final var metal = project.getExtensions().findByType(MetalExtension.class);
        final var objects = project.getObjects();
        final var tasks = project.getTasks();

        // prepare configuration
        final var commandsDirectory = layout.getBuildDirectory().dir("db/%s/asm".formatted(name));
        final var compileOptions = objects.listProperty(String.class).convention(metal.getCompileOptions());
        final var includables = configurations.named(INCLUDABLE_DEPENDENCIES);
        final var sources = objects.sourceDirectorySet(name,name);
        sources.srcDir(layout.getProjectDirectory().dir("src/%s/asm".formatted(name)));
        final var objectDirectory = layout.getBuildDirectory().dir("obj/%s/asm".formatted(name));

        // register commands database task
        final var commandsTask = tasks.register("commands-%s-asm".formatted(name), MetalAsmCommandsTask.class, task ->
        {
            task.getCompileOptions().set(compileOptions);
            task.getIncludables().from(includables);
            task.getObjectDirectory().set(objectDirectory.map(Directory::getAsFile));
            task.getOutputDirectory().set(commandsDirectory);
            task.setSource(sources);
        });
        configurations.named(COMMANDS_ELEMENTS).configure(it -> it.getOutgoing().artifact(commandsTask));

        // register compile task
        final var compileTask = tasks.register("compile-%s-asm".formatted(name), MetalAsmCompileTask.class, task ->
        {
            task.getCompileOptions().set(compileOptions);
            task.getIncludables().from(includables);
            task.getOutputDirectory().set(objectDirectory);
            task.setSource(sources);
        });
        tasks.named("compile").configure(it -> it.dependsOn(compileTask));

        return new MetalAsmSources(commandsTask, compileOptions, compileTask, name);
    }
}
