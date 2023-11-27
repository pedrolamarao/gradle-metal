package br.dev.pedrolamarao.gradle.metal;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.BasePlugin;

public class MetalLibraryPlugin implements Plugin<Project>
{
    @Override
    public void apply (Project project)
    {
        final var configurations = project.getConfigurations();
        final var layout = project.getLayout();
        final var name = project.getName();
        final var objects = project.getObjects();
        final var plugins = project.getPluginManager();
        final var tasks = project.getTasks();

        plugins.apply(BasePlugin.class);
        plugins.apply(MetalBasePlugin.class);

        final var api = configurations.dependencyScope("api", configuration -> {
            configuration.setDescription("application api dependencies");
        });
        configurations.dependencyScope("implementation", configuration -> {
            configuration.setDescription("application implementation dependencies");
            configuration.extendsFrom(api.get());
        });

        final var library = project.getExtensions().create("library",MetalLibrary.class);
        final var compileOptions = library.getCompileOptions();

        final var objectFiles = objects.fileCollection();

        final var archiveTask = tasks.register("archive",MetalArchive.class,archive ->
        {
            final var target = archive.getTarget().get();
            final var archiveName = archive.getMetal().get().archiveFileName(target);
            archive.getOutput().set( layout.getBuildDirectory().file("lib/main/%s/%s".formatted(target,archiveName)) );
            archive.setSource(objectFiles);
        });
        plugins.withPlugin("br.dev.pedrolamarao.metal.asm",asm ->
        {
            final var compileTask = tasks.register("compileAsm",MetalAsmCompile.class,compile ->
            {
                compile.getOutputDirectory().set(layout.getBuildDirectory().dir("obj/main/asm"));
                compile.getOptions().convention(compileOptions);
                compile.setSource(layout.getProjectDirectory().dir("src/main/asm"));
            });
            objectFiles.from(compileTask);
        });
        plugins.withPlugin("br.dev.pedrolamarao.metal.c",c ->
        {
            final var compileTask = tasks.register("compileC",MetalCCompile.class,compile ->
            {
                compile.getOutputDirectory().set(layout.getBuildDirectory().dir("obj/main/c"));
                compile.getOptions().convention(compileOptions);
                compile.setSource(layout.getProjectDirectory().dir("src/main/c"));
            });
            objectFiles.from(compileTask);
        });
        plugins.withPlugin("br.dev.pedrolamarao.metal.cxx",cxx ->
        {
            final var compileTask = tasks.register("compileCxx",MetalCxxCompile.class,compile ->
            {
                compile.getOutputDirectory().set(layout.getBuildDirectory().dir("obj/main/cxx"));
                compile.getOptions().convention(compileOptions);
                compile.setSource(layout.getProjectDirectory().dir("src/main/cxx"));
            });
            objectFiles.from(compileTask);
        });

        configurations.consumable(Metal.LINKABLE_ELEMENTS, configuration -> {
            configuration.attributes(it -> {
                it.attribute(MetalCapability.ATTRIBUTE, MetalCapability.LINKABLE);
                it.attribute(MetalVisibility.ATTRIBUTE, MetalVisibility.COMPILE);
            });
            configuration.extendsFrom(api.get());
            configuration.setDescription("library linkable elements");
            configuration.getOutgoing().artifact(archiveTask);
        });
    }
}
