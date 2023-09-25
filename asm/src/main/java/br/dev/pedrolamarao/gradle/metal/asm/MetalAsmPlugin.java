package br.dev.pedrolamarao.gradle.metal.asm;

import br.dev.pedrolamarao.gradle.metal.base.MetalExtension;
import br.dev.pedrolamarao.gradle.metal.base.NativeBasePlugin;
import br.dev.pedrolamarao.gradle.metal.cpp.MetalCppPlugin;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

import static br.dev.pedrolamarao.gradle.metal.cpp.MetalCppPlugin.CPP_INCLUDABLES;

public class MetalAsmPlugin implements Plugin<Project>
{
    @Override
    public void apply (Project project)
    {
        project.getPluginManager().apply(NativeBasePlugin.class);
        project.getPluginManager().apply(MetalCppPlugin.class);

        final var metal = project.getExtensions().getByType(MetalExtension.class);

        final var asm = project.getObjects().domainObjectContainer(MetalAsmSources.class, name -> createSources(project,name));
        metal.getExtensions().create("asm", MetalAsmExtension.class, asm);
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

        headers.from( providers.provider(() -> configurations.maybeCreate(CPP_INCLUDABLES.apply(name)).getArtifacts().getFiles() ) );

        final var compileTask = tasks.register("compile-%s-asm".formatted(name), AsmCompileTask.class, task ->
        {
            task.getCompileOptions().set(compileOptions);
            task.getHeaderDependencies().from(headers);
            task.getOutputDirectory().set(objectDirectory);
            task.setSource(sources);
        });

        return new MetalAsmSources(compileOptions, headers, name, sources);
    }
}
