package br.dev.pedrolamarao.gradle.metal.asm;

import br.dev.pedrolamarao.gradle.metal.base.MetalBasePlugin;
import br.dev.pedrolamarao.gradle.metal.base.MetalExtension;
import br.dev.pedrolamarao.gradle.metal.cpp.MetalCppPlugin;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.Directory;

import static br.dev.pedrolamarao.gradle.metal.base.MetalBasePlugin.COMMANDS_ELEMENTS;

public class MetalAsmPlugin implements Plugin<Project>
{
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
        final var objects = project.getObjects();
        final var providers = project.getProviders();
        final var tasks = project.getTasks();

        final var compileOptions = objects.listProperty(String.class);
        final var headers = objects.fileCollection();
        final var sources = objects.sourceDirectorySet(name,name);
        sources.srcDir(layout.getProjectDirectory().dir("src/%s/asm".formatted(name)));
        final var objectDirectory = layout.getBuildDirectory().dir("obj/%s/asm".formatted(name));

        final var commandsTask = tasks.register("commands-%s-asm".formatted(name), MetalAsmCommandsTask.class, task ->
        {
            task.getCompileOptions().set(compileOptions);
            task.getHeaderDependencies().from(headers);
            task.getObjectDirectory().set(objectDirectory.map(Directory::getAsFile));
            task.getOutputDirectory().set(layout.getBuildDirectory().dir("db/%s/asm".formatted(name)));
            task.setSource(sources);
        });

        configurations.named(COMMANDS_ELEMENTS).configure(configuration -> {
            configuration.getOutgoing().artifact(commandsTask.flatMap(MetalAsmCommandsTask::getOutputFile), it -> it.builtBy(commandsTask));
        });

        final var compileTask = tasks.register("compile-%s-asm".formatted(name), MetalAsmCompileTask.class, task ->
        {
            task.getCompileOptions().set(compileOptions);
            task.getHeaderDependencies().from(headers);
            task.getOutputDirectory().set(objectDirectory);
            task.setSource(sources);
        });

        return new MetalAsmSources(commandsTask, compileOptions, compileTask, headers, name, sources);
    }
}
