package br.dev.pedrolamarao.gradle.metal;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.BasePlugin;

public class MetalApplicationPlugin implements Plugin<Project>
{
    @Override
    public void apply (Project project)
    {
        final var configurations = project.getConfigurations();
        final var layout = project.getLayout();
        final var name = project.getName();
        final var plugins = project.getPluginManager();
        final var tasks = project.getTasks();

        plugins.apply(BasePlugin.class);
        plugins.apply(MetalBasePlugin.class);

        final var implementation = configurations.dependencyScope("implementation", configuration -> {
            configuration.setDescription("application implementation dependencies");
        });
        final var linkableDependencies = configurations.resolvable(Metal.LINKABLE_DEPENDENCIES, configuration -> {
            configuration.attributes(it -> {
                it.attribute(MetalCapability.ATTRIBUTE, MetalCapability.LINKABLE);
                it.attribute(MetalVisibility.ATTRIBUTE, MetalVisibility.COMPILE);
            });
            configuration.extendsFrom(implementation.get());
            configuration.setDescription("metal linkable dependencies");
        });

        final var application = project.getExtensions().create("application",MetalApplication.class);
        final var compileOptions = application.getCompileOptions();
        final var linkOptions = application.getLinkOptions();
        final var applicationObjects = project.getObjects().fileCollection();

        final var linkTask = tasks.register("link",MetalLink.class,link ->
        {
            final var applicationName = link.getMetal().zip(link.getTarget(),(metal,target) -> metal.executableFileName(target,name));
            link.getLinkableDependencies().from(linkableDependencies);
            link.getOutput().set( layout.getBuildDirectory().zip(link.getTarget(),(dir,target) -> dir.file("exe/main/%s/%s".formatted(target,applicationName.get()))) );
            link.getOptions().convention(linkOptions);
            link.setSource(applicationObjects);
        });
        plugins.withPlugin("br.dev.pedrolamarao.metal.asm",asm ->
        {
            final var compileTask = tasks.register("compileAsm",MetalAsmCompile.class,compile ->
            {
                compile.getOutputDirectory().set(layout.getBuildDirectory().dir("obj/main/asm"));
                compile.getOptions().convention(compileOptions);
                compile.setSource(layout.getProjectDirectory().dir("src/main/asm"));
            });
            applicationObjects.from(compileTask);
        });
        plugins.withPlugin("br.dev.pedrolamarao.metal.c",c ->
        {
            final var compileTask = tasks.register("compileC",MetalCCompile.class,compile ->
            {
                compile.getOutputDirectory().set(layout.getBuildDirectory().dir("obj/main/c"));
                compile.getOptions().convention(compileOptions);
                compile.setSource(layout.getProjectDirectory().dir("src/main/c"));
            });
            applicationObjects.from(compileTask);
        });
        plugins.withPlugin("br.dev.pedrolamarao.metal.cxx",cxx ->
        {
            final var compileTask = tasks.register("compileCxx",MetalCxxCompile.class,compile ->
            {
                compile.getOutputDirectory().set(layout.getBuildDirectory().dir("obj/main/cxx"));
                compile.getOptions().convention(compileOptions);
                compile.setSource(layout.getProjectDirectory().dir("src/main/cxx"));
            });
            applicationObjects.from(compileTask);
        });
    }
}
