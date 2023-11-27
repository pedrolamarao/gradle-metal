package br.dev.pedrolamarao.gradle.metal;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.BasePlugin;

import java.util.HashSet;

public class MetalApplicationPlugin implements Plugin<Project>
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

        final var implementation = configurations.dependencyScope("implementation", configuration -> {
            configuration.setDescription("application implementation dependencies");
        });
        final var includableDependencies = configurations.resolvable(Metal.INCLUDABLE_DEPENDENCIES, configuration -> {
            configuration.attributes(it -> {
                it.attribute(MetalCapability.ATTRIBUTE, MetalCapability.INCLUDABLE);
                it.attribute(MetalVisibility.ATTRIBUTE, MetalVisibility.COMPILE);
            });
            configuration.extendsFrom(implementation.get());
            configuration.setDescription("application includable dependencies");
        });
        final var linkableDependencies = configurations.resolvable(Metal.LINKABLE_DEPENDENCIES, configuration -> {
            configuration.attributes(it -> {
                it.attribute(MetalCapability.ATTRIBUTE, MetalCapability.LINKABLE);
                it.attribute(MetalVisibility.ATTRIBUTE, MetalVisibility.COMPILE);
            });
            configuration.extendsFrom(implementation.get());
            configuration.setDescription("application includable dependencies");
        });

        final var application = project.getExtensions().create("application",MetalApplication.class);
        final var compileOptions = application.getCompileOptions();
        final var includeDir = layout.getProjectDirectory().dir("src/main/cpp");
        final var includePath = includableDependencies.map(it -> {
            final var list = new HashSet<String>();
            list.add(includeDir.toString());
            it.getElements().get().forEach(element -> list.add(element.toString()));
            return list;
        });
        final var linkOptions = application.getLinkOptions();
        final var objectFiles = objects.fileCollection();

        final var linkTask = tasks.register("link",MetalLink.class,link ->
        {
            final var applicationName = link.getMetal().zip(link.getTarget(),(metal,target) -> metal.executableFileName(target,name));
            final var applicationFile = layout.getBuildDirectory().zip(link.getTarget(),(dir,target) -> dir.file("exe/main/%s/%s".formatted(target,applicationName.get())));
            link.getLinkableDependencies().from(linkableDependencies);
            link.getOutput().convention(applicationFile);
            link.getOptions().convention(linkOptions);
            link.setSource(objectFiles);
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
                compile.getIncludePath().convention(includePath);
                compile.getOutputDirectory().convention(layout.getBuildDirectory().dir("obj/main/c"));
                compile.getOptions().convention(compileOptions);
                compile.setSource(layout.getProjectDirectory().dir("src/main/c"));
            });
            objectFiles.from(compileTask);
        });
        plugins.withPlugin("br.dev.pedrolamarao.metal.cxx",cxx ->
        {
            final var compileTask = tasks.register("compileCxx",MetalCxxCompile.class,compile ->
            {
                compile.getIncludePath().convention(includePath);
                compile.getOutputDirectory().convention(layout.getBuildDirectory().dir("obj/main/cxx"));
                compile.getOptions().convention(compileOptions);
                compile.setSource(layout.getProjectDirectory().dir("src/main/cxx"));
            });
            objectFiles.from(compileTask);
        });
    }
}
