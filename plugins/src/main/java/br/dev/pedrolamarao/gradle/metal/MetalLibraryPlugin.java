// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.tasks.Exec;

import java.nio.file.Files;
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
            configuration.setDescription("library api dependencies");
        });

        final var implementation = configurations.dependencyScope("implementation", configuration -> {
            configuration.setDescription("library implementation dependencies");
            configuration.extendsFrom(api.get());
        });

        final var importDependencies = configurations.resolvable(Metal.IMPORTABLE_DEPENDENCIES, configuration -> {
            configuration.attributes(it -> {
                it.attribute(MetalCapability.ATTRIBUTE, MetalCapability.IMPORTABLE);
                it.attribute(MetalVisibility.ATTRIBUTE, MetalVisibility.COMPILE);
            });
            configuration.extendsFrom(implementation.get());
            configuration.setDescription("library include dependencies");
        });

        final var includeDependencies = configurations.resolvable(Metal.INCLUDABLE_DEPENDENCIES, configuration -> {
            configuration.attributes(it -> {
                it.attribute(MetalCapability.ATTRIBUTE, MetalCapability.INCLUDABLE);
                it.attribute(MetalVisibility.ATTRIBUTE, MetalVisibility.COMPILE);
            });
            configuration.extendsFrom(implementation.get());
            configuration.setDescription("library import dependencies");
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

        final var testImplementation = configurations.dependencyScope("testImplementation", configuration -> {
            configuration.setDescription("test implementation dependencies");
            configuration.extendsFrom(implementation.get());
        });

        final var testImportDependencies = configurations.resolvable("testImportDependencies", configuration -> {
            configuration.attributes(it -> {
                it.attribute(MetalCapability.ATTRIBUTE, MetalCapability.IMPORTABLE);
                it.attribute(MetalVisibility.ATTRIBUTE, MetalVisibility.COMPILE);
            });
            configuration.extendsFrom(testImplementation.get());
            configuration.setDescription("test include dependencies");
        });

        final var testIncludeDependencies = configurations.resolvable("testIncludeDependencies", configuration -> {
            configuration.attributes(it -> {
                it.attribute(MetalCapability.ATTRIBUTE, MetalCapability.INCLUDABLE);
                it.attribute(MetalVisibility.ATTRIBUTE, MetalVisibility.COMPILE);
            });
            configuration.extendsFrom(testImplementation.get());
            configuration.setDescription("test include dependencies");
        });

        final var testLinkDependencies = configurations.resolvable("testLinkDependencies", configuration -> {
            configuration.attributes(it -> {
                it.attribute(MetalCapability.ATTRIBUTE, MetalCapability.LINKABLE);
                it.attribute(MetalVisibility.ATTRIBUTE, MetalVisibility.COMPILE);
            });
            configuration.extendsFrom(testImplementation.get());
            configuration.setDescription("test link dependencies");
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

        final var testImportPath = testImportDependencies.map(it -> {
            final var list = new HashSet<String>();
            it.getElements().get().forEach(element -> list.add(element.toString()));
            return list;
        });
        final var testIncludePath = testIncludeDependencies.map(it -> {
            final var list = new HashSet<String>();
            list.add(includeDir.toString());
            it.getElements().get().forEach(element -> list.add(element.toString()));
            return list;
        });
        final var testObjectFiles = objects.fileCollection();

        // tasks

        includableElements.configure(it -> it.getOutgoing().artifact(includeDir));

        plugins.withPlugin("br.dev.pedrolamarao.metal.asm",asm ->
        {
            // main

            {
                final var compileTask = tasks.register("compileAsm", MetalAsmCompile.class, compile ->
                {
                    compile.getOutputDirectory().convention(layout.getBuildDirectory().dir("obj/main/asm"));
                    compile.getOptions().convention(compileOptions);
                    compile.setSource(layout.getProjectDirectory().dir("src/main/asm"));
                });
                objectFiles.from(compileTask);
            }

            // test

            {
                final var compileTask = tasks.register("compileTestAsm", MetalAsmCompile.class, compile ->
                {
                    compile.getOutputDirectory().convention(layout.getBuildDirectory().dir("obj/test/asm"));
                    compile.getOptions().convention(compileOptions);
                    compile.setSource(layout.getProjectDirectory().dir("src/test/asm"));
                });
                testObjectFiles.from(compileTask);
            }
        });

        plugins.withPlugin("br.dev.pedrolamarao.metal.c",c ->
        {
            // main

            {
                final var compileTask = tasks.register("compileC", MetalCCompile.class, compile ->
                {
                    compile.dependsOn(includeDependencies.map(Configuration::getBuildDependencies));
                    compile.getIncludePath().convention(includePath);
                    compile.getOutputDirectory().convention(layout.getBuildDirectory().dir("obj/main/c"));
                    compile.getOptions().convention(compileOptions);
                    compile.setSource(layout.getProjectDirectory().dir("src/main/c"));
                });
                objectFiles.from(compileTask);
            }

            // test

            {
                final var compileTask = tasks.register("compileTestC", MetalCCompile.class, compile ->
                {
                    compile.dependsOn(testIncludeDependencies.map(Configuration::getBuildDependencies));
                    compile.getIncludePath().convention(testIncludePath);
                    compile.getOutputDirectory().convention(layout.getBuildDirectory().dir("obj/test/c"));
                    compile.getOptions().convention(compileOptions);
                    compile.setSource(layout.getProjectDirectory().dir("src/test/c"));
                });
                testObjectFiles.from(compileTask);
            }
        });

        plugins.withPlugin("br.dev.pedrolamarao.metal.cxx",cxx ->
        {
            // main

            {
                final var precompileTask = tasks.register("precompileIxx", MetalIxxPrecompile.class, precompile ->
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
                    it.getOutgoing().artifact(precompileTask.map(MetalIxxPrecompile::getOutputDirectory), it2 ->
                        it2.builtBy(precompileTask)
                    )
                );

                final var imports = precompileTask.zip(importPath, (precompile, dependencies) -> {
                    final var list = new ArrayList<String>();
                    list.add(precompile.getOutputDirectory().get().toString());
                    list.addAll(dependencies);
                    return list;
                });

                final var source = objects.fileCollection();
                source.from(layout.getProjectDirectory().dir("src/main/cxx"));
                source.from(precompileTask);

                final var compileTask = tasks.register("compileCxx", MetalCxxCompile.class, compile ->
                {
                    compile.getImportPath().convention(imports);
                    compile.getIncludePath().convention(includePath);
                    compile.getOutputDirectory().convention(layout.getBuildDirectory().dir("obj/main/cxx"));
                    compile.getOptions().convention(compileOptions);
                    compile.setSource(source);
                });

                objectFiles.from(compileTask);
            }

            // test

            {
                final var precompileTask = tasks.register("precompileTestIxx", MetalIxxPrecompile.class, precompile ->
                {
                    precompile.dependsOn(
                        testIncludeDependencies.map(Configuration::getBuildDependencies),
                        testImportDependencies.map(Configuration::getBuildDependencies)
                    );
                    precompile.getImportPath().convention(testImportPath);
                    precompile.getIncludePath().convention(testIncludePath);
                    precompile.getOutputDirectory().convention(layout.getBuildDirectory().dir("bmi/test/ixx"));
                    precompile.getOptions().convention(compileOptions);
                    precompile.setSource(layout.getProjectDirectory().dir("src/test/ixx"));
                });
                importableElements.configure(it ->
                    it.getOutgoing().artifact(precompileTask.map(MetalIxxPrecompile::getOutputDirectory), it2 ->
                        it2.builtBy(precompileTask)
                    )
                );

                final var imports = precompileTask.zip(testImportPath, (precompile, dependencies) -> {
                    final var list = new ArrayList<String>();
                    list.add(tasks.named("precompileIxx",MetalIxxPrecompile.class).get().getOutputDirectory().get().toString());
                    list.add(precompile.getOutputDirectory().get().toString());
                    list.addAll(dependencies);
                    return list;
                });

                final var source = objects.fileCollection();
                source.from(layout.getProjectDirectory().dir("src/test/cxx"));
                source.from(precompileTask);

                final var compileTask = tasks.register("compileTestCxx", MetalCxxCompile.class, compile ->
                {
                    compile.getImportPath().convention(imports);
                    compile.getIncludePath().convention(testIncludePath);
                    compile.getOutputDirectory().convention(layout.getBuildDirectory().dir("obj/test/cxx"));
                    compile.getOptions().convention(compileOptions);
                    compile.setSource(source);
                });

                testObjectFiles.from(compileTask);
            }
        });

        final var archiveTask = tasks.register("archive",MetalArchive.class,archive ->
        {
            final var archiveName = archive.getTarget().map(target -> Metal.archiveFileName(target,name));
            final var archiveFile = layout.getBuildDirectory().zip(archive.getTarget(),(dir,target) -> dir.file("lib/main/%s/%s".formatted(target,archiveName.get())));
            archive.getOutput().convention(archiveFile);
            archive.setSource(objectFiles);
        });
        linkableElements.configure(it -> it.getOutgoing().artifact(archiveTask));

        final var linkTestTask = tasks.register("linkTest",MetalLink.class,link ->
        {
            final var applicationName = link.getTarget().map(target -> Metal.executableFileName(target,name));
            final var applicationFile = layout.getBuildDirectory().zip(link.getTarget(),(dir,target) ->
                dir.file("exe/test/%s/%s".formatted(target,applicationName.get()))
            );
            link.getLinkDependencies().from(archiveTask);
            link.getLinkDependencies().from(testLinkDependencies);
            link.getOutput().convention(applicationFile);
            link.setSource(testObjectFiles);
        });

        final var runTestTask = tasks.register("runTest", Exec.class, exec ->
        {
            final var executable = linkTestTask.flatMap(MetalLink::getOutput);
            exec.onlyIf("executable file exists",spec -> Files.exists(executable.get().getAsFile().toPath()));
            exec.getInputs().file(executable);
            exec.setExecutable(executable.get());
        });

        tasks.named("assemble",it -> it.dependsOn(archiveTask));

        tasks.named("check",it -> it.dependsOn(runTestTask));
    }
}
