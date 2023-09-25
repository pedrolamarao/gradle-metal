package br.dev.pedrolamarao.gradle.metal.c;

import br.dev.pedrolamarao.gradle.metal.base.MetalExtension;
import br.dev.pedrolamarao.gradle.metal.base.NativeBasePlugin;
import br.dev.pedrolamarao.gradle.metal.cpp.MetalCppPlugin;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

import static br.dev.pedrolamarao.gradle.metal.cpp.MetalCppPlugin.CPP_INCLUDABLES;

public class MetalCPlugin implements Plugin<Project>
{
    @Override
    public void apply (Project project)
    {
        project.getPluginManager().apply(NativeBasePlugin.class);
        project.getPluginManager().apply(MetalCppPlugin.class);

        final var metal = project.getExtensions().getByType(MetalExtension.class);

        final var c = project.getObjects().domainObjectContainer(MetalCSources.class, name -> createSources(project,name));
        metal.getExtensions().create("c", MetalCExtension.class, c);
    }

    static MetalCSources createSources (Project project, String name)
    {
        final var configurations = project.getConfigurations();
        final var layout = project.getLayout();
        final var objects = project.getObjects();
        final var providers = project.getProviders();
        final var tasks = project.getTasks();

        final var compileOptions = objects.listProperty(String.class);
        final var headers = objects.fileCollection();
        final var sources = objects.sourceDirectorySet(name,name);
        sources.srcDir(layout.getProjectDirectory().dir("src/%s/c".formatted(name)));
        final var objectDirectory = layout.getBuildDirectory().dir("obj/%s/c".formatted(name));

        headers.from( providers.provider(() -> configurations.maybeCreate(CPP_INCLUDABLES.apply(name)).getArtifacts().getFiles() ) );

        final var compileTask = tasks.register("compile-%s-c".formatted(name), CCompileTask.class, task ->
        {
            task.getCompileOptions().set(compileOptions);
            task.getHeaderDependencies().from(headers);
            task.getOutputDirectory().set(objectDirectory);
            task.setSource(sources);
        });

        return new MetalCSources(compileOptions, headers, name, sources);
    }
}
