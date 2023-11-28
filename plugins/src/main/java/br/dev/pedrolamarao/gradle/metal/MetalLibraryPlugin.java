package br.dev.pedrolamarao.gradle.metal;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.plugins.BasePlugin;

import java.util.ArrayList;
import java.util.HashSet;

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

        // configurations

        final var api = configurations.dependencyScope("api", configuration -> {
            configuration.setDescription("application api dependencies");
        });

        final var implementation = configurations.dependencyScope("implementation", configuration -> {
            configuration.setDescription("application implementation dependencies");
            configuration.extendsFrom(api.get());
        });

        final var importDependencies = configurations.resolvable(Metal.IMPORTABLE_DEPENDENCIES, configuration -> {
            configuration.attributes(it -> {
                it.attribute(MetalCapability.ATTRIBUTE, MetalCapability.IMPORTABLE);
                it.attribute(MetalVisibility.ATTRIBUTE, MetalVisibility.COMPILE);
            });
            configuration.extendsFrom(implementation.get());
            configuration.setDescription("application includable dependencies");
        });

        final var includeDependencies = configurations.resolvable(Metal.INCLUDABLE_DEPENDENCIES, configuration -> {
            configuration.attributes(it -> {
                it.attribute(MetalCapability.ATTRIBUTE, MetalCapability.INCLUDABLE);
                it.attribute(MetalVisibility.ATTRIBUTE, MetalVisibility.COMPILE);
            });
            configuration.extendsFrom(implementation.get());
            configuration.setDescription("application includable dependencies");
        });

        final var importableElements = configurations.consumable(Metal.IMPORTABLE_ELEMENTS, configuration -> {
            configuration.attributes(it -> {
                it.attribute(MetalCapability.ATTRIBUTE, MetalCapability.IMPORTABLE);
                it.attribute(MetalVisibility.ATTRIBUTE, MetalVisibility.COMPILE);
            });
            configuration.extendsFrom(api.get());
            configuration.setDescription("library importable elements");
        });

        final var includableElements = configurations.consumable(Metal.INCLUDABLE_ELEMENTS, configuration -> {
            configuration.attributes(it -> {
                it.attribute(MetalCapability.ATTRIBUTE, MetalCapability.INCLUDABLE);
                it.attribute(MetalVisibility.ATTRIBUTE, MetalVisibility.COMPILE);
            });
            configuration.extendsFrom(api.get());
            configuration.setDescription("library includable elements");
        });

        final var linkableElements = configurations.consumable(Metal.LINKABLE_ELEMENTS, configuration -> {
            configuration.attributes(it -> {
                it.attribute(MetalCapability.ATTRIBUTE, MetalCapability.LINKABLE);
                it.attribute(MetalVisibility.ATTRIBUTE, MetalVisibility.COMPILE);
            });
            configuration.extendsFrom(api.get());
            configuration.setDescription("library linkable elements");
        });

        // model

        final var library = project.getExtensions().create("library",MetalLibrary.class);
        final var compileOptions = library.getCompileOptions();
        final var importPath = importDependencies.map(it -> {
            final var list = new HashSet<String>();
            it.getElements().get().forEach(element -> list.add(element.toString()));
            return list;
        });
        final var includeDir = layout.getProjectDirectory().dir("src/main/cpp");
        final var includePath = includeDependencies.map(it -> {
            final var list = new HashSet<String>();
            list.add(includeDir.toString());
            it.getElements().get().forEach(element -> list.add(element.toString()));
            return list;
        });
        final var objectFiles = objects.fileCollection();

        // tasks

        includableElements.configure(it -> it.getOutgoing().artifact(includeDir));

        plugins.withPlugin("br.dev.pedrolamarao.metal.asm",asm ->
        {
            final var compileTask = tasks.register("compileAsm",MetalAsmCompile.class,compile ->
            {
                compile.getOutputDirectory().convention(layout.getBuildDirectory().dir("obj/main/asm"));
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
            importableElements.configure(it ->
                it.getOutgoing().artifact(precompileTask.map(MetalIxxPrecompile::getOutputDirectory),it2 ->
                    it2.builtBy(precompileTask)
                )
            );

            final var compileImports = precompileTask.zip(importPath,(precompile,dependencies) -> {
                final var list = new ArrayList<String>();
                list.add(precompile.getOutputDirectory().get().toString());
                list.addAll(dependencies);
                return list;
            });
            final var compileSource = objects.fileCollection();
            compileSource.from(layout.getProjectDirectory().dir("src/main/cxx"));
            compileSource.from(precompileTask);
            final var compileTask = tasks.register("compileCxx",MetalCxxCompile.class,compile ->
            {
                compile.getImportPath().convention(compileImports);
                compile.getIncludePath().convention(includePath);
                compile.getOutputDirectory().convention(layout.getBuildDirectory().dir("obj/main/cxx"));
                compile.getOptions().convention(compileOptions);
                compile.setSource(compileSource);
            });
            objectFiles.from(compileTask);
        });

        final var archiveTask = tasks.register("archive",MetalArchive.class,archive ->
        {
            final var archiveName = archive.getMetal().zip(archive.getTarget(),(metal,target) -> metal.archiveFileName(target,name));
            final var archiveFile = layout.getBuildDirectory().zip(archive.getTarget(),(dir,target) -> dir.file("lib/main/%s/%s".formatted(target,archiveName.get())));
            archive.getOutput().convention(archiveFile);
            archive.setSource(objectFiles);
        });
        linkableElements.configure(it -> it.getOutgoing().artifact(archiveTask));

        tasks.named("assemble",it -> {
            it.dependsOn(archiveTask);
        });
    }
}
