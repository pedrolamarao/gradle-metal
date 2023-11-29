package br.dev.pedrolamarao.gradle.metal;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.tasks.Exec;

import java.nio.file.Files;
import java.util.ArrayList;
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

        // configurations

        final var implementation = configurations.dependencyScope("implementation", configuration -> {
            configuration.setDescription("application implementation dependencies");
        });

        final var importDependencies = configurations.resolvable(Metal.IMPORTABLE_DEPENDENCIES, configuration -> {
            configuration.attributes(it -> {
                it.attribute(MetalCapability.ATTRIBUTE, MetalCapability.IMPORTABLE);
                it.attribute(MetalVisibility.ATTRIBUTE, MetalVisibility.COMPILE);
            });
            configuration.extendsFrom(implementation.get());
            configuration.setDescription("application import dependencies");
        });

        final var includeDependencies = configurations.resolvable(Metal.INCLUDABLE_DEPENDENCIES, configuration -> {
            configuration.attributes(it -> {
                it.attribute(MetalCapability.ATTRIBUTE, MetalCapability.INCLUDABLE);
                it.attribute(MetalVisibility.ATTRIBUTE, MetalVisibility.COMPILE);
            });
            configuration.extendsFrom(implementation.get());
            configuration.setDescription("application include dependencies");
        });

        final var linkDependencies = configurations.resolvable(Metal.LINKABLE_DEPENDENCIES, configuration -> {
            configuration.attributes(it -> {
                it.attribute(MetalCapability.ATTRIBUTE, MetalCapability.LINKABLE);
                it.attribute(MetalVisibility.ATTRIBUTE, MetalVisibility.COMPILE);
            });
            configuration.extendsFrom(implementation.get());
            configuration.setDescription("application link dependencies");
        });

        // model

        final var application = project.getExtensions().create("application",MetalApplication.class);
        final var compileOptions = application.getCompileOptions();
        final var importPath = importDependencies.map(it -> {
            final var list = new HashSet<String>();
            it.getElements().get().forEach(element -> list.add(element.toString()));
            return list;
        });
        final var includePath = includeDependencies.map(it -> {
            final var list = new HashSet<String>();
            list.add(layout.getProjectDirectory().dir("src/main/cpp").toString());
            it.getElements().get().forEach(element -> list.add(element.toString()));
            return list;
        });
        final var linkOptions = application.getLinkOptions();
        final var objectFiles = objects.fileCollection();

        // tasks

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
                compile.dependsOn(includeDependencies.map(Configuration::getBuildDependencies));
                compile.getIncludePath().convention(includePath);
                compile.getOutputDirectory().convention(layout.getBuildDirectory().dir("obj/main/c"));
                compile.getOptions().convention(compileOptions);
                compile.setSource(layout.getProjectDirectory().dir("src/main/c"));
            });
            objectFiles.from(compileTask);
        });

        plugins.withPlugin("br.dev.pedrolamarao.metal.cxx",cxx ->
        {
            final var precompileTask = tasks.register("precompileIxx",MetalIxxPrecompile.class,precompile ->
            {
                precompile.dependsOn(
                    includeDependencies.map(Configuration::getBuildDependencies),
                    importDependencies.map(Configuration::getBuildDependencies)
                );
                precompile.getImportPath().convention(importPath);
                precompile.getIncludePath().convention(includePath);
                precompile.getOutputDirectory().convention(layout.getBuildDirectory().dir("bmi/main/ixx"));
                precompile.getOptions().convention(compileOptions);
                precompile.setSource(layout.getProjectDirectory().dir("src/main/ixx"));
            });

            final var compileImports = precompileTask.zip(importPath,(precompile,dependencies) -> {
                final var list = new ArrayList<String>();
                list.add(precompile.getOutputDirectory().get().toString());
                list.addAll(dependencies);
                return list;
            });
            final var compileSources = objects.fileCollection();
            compileSources.from(layout.getProjectDirectory().dir("src/main/cxx"));
            compileSources.from(precompileTask);
            final var compileTask = tasks.register("compileCxx",MetalCxxCompile.class,compile ->
            {
                compile.getImportPath().convention(compileImports);
                compile.getIncludePath().convention(includePath);
                compile.getOutputDirectory().convention(layout.getBuildDirectory().dir("obj/main/cxx"));
                compile.getOptions().convention(compileOptions);
                compile.setSource(compileSources);
            });
            objectFiles.from(compileTask);
        });

        final var linkTask = tasks.register("link",MetalLink.class,link ->
        {
            final var applicationName = link.getMetal().zip(link.getTarget(),(metal,target) ->
                metal.executableFileName(target,name)
            );
            final var applicationFile = layout.getBuildDirectory().zip(link.getTarget(),(dir,target) ->
                dir.file("exe/main/%s/%s".formatted(target,applicationName.get()))
            );
            link.dependsOn(linkDependencies.map(Configuration::getBuildDependencies));
            link.getLinkDependencies().from(linkDependencies);
            link.getOutput().convention(applicationFile);
            link.getOptions().convention(linkOptions);
            link.setSource(objectFiles);
        });

        final var runTask = tasks.register("run",Exec.class,exec ->
        {
            final var executable = linkTask.flatMap(MetalLink::getOutput);
            exec.onlyIf("executable file exists",spec -> Files.exists(executable.get().getAsFile().toPath()));
            exec.getInputs().file(executable);
            exec.setExecutable(executable.get());
        });

        tasks.named("assemble",it -> {
            it.dependsOn(linkTask);
        });
    }
}
