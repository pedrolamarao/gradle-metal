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

/**
 * Gradle Metal application plugin.
 */
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

        final var buildDirectory = layout.getBuildDirectory();

        plugins.apply(BasePlugin.class);
        plugins.apply(MetalBasePlugin.class);

        // configurations

        final var implementation = configurations.named("implementation");

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

        final var commandsElements = configurations.named(Metal.COMMANDS_ELEMENTS);

        // model

        final var application = project.getExtensions().create("application",MetalApplication.class);
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
        final var objectFiles = objects.fileCollection();

        // tasks

        plugins.withPlugin("br.dev.pedrolamarao.metal.asm",asm ->
        {
            final var source = layout.getProjectDirectory().dir("src/main/asm");

            final var compileTask = tasks.register("compileAsm",MetalAsmCompile.class,compile ->
            {
                final var target = compile.getMetal().map(MetalService::getTarget);
                final var targets = application.getTargets();
                compile.getOutputDirectory().set(buildDirectory.dir("obj/main/asm"));
                compile.getOptions().convention(application.getCompileOptions());
                compile.setSource(source);
                compile.onlyIf("target is enabled",it ->
                    targets.zip(target,(list,item) -> list.isEmpty() || list.contains(item)).get()
                );
            });
            objectFiles.from(compileTask);

            final var commandsTask = tasks.register("compileAsmCommands",MetalCompileCommands.class,task ->
            {
                final var output = buildDirectory.file( task.getTarget().map("commands/main/asm/%s/commands.json"::formatted) );
                task.getCompiler().convention(compileTask.flatMap(MetalCompile::getCompiler));
                task.getOptions().convention(compileTask.flatMap(MetalCompile::getInternalOptions));
                task.getCompileDirectory().convention(compileTask.map(it -> it.getTargetOutputDirectory().get().getAsFile()));
                task.setSource(source);
                task.getOutput().convention(output);
            });
            commandsElements.configure(it -> it.getOutgoing().artifact(commandsTask));
        });

        plugins.withPlugin("br.dev.pedrolamarao.metal.c",c ->
        {
            final var source = layout.getProjectDirectory().dir("src/main/c");

            final var compileTask = tasks.register("compileC",MetalCCompile.class,compile ->
            {
                final var target = compile.getMetal().map(MetalService::getTarget);
                final var targets = application.getTargets();
                compile.dependsOn(includeDependencies.map(Configuration::getBuildDependencies));
                compile.getIncludePath().convention(includePath);
                compile.getOutputDirectory().convention(buildDirectory.dir("obj/main/c"));
                compile.getOptions().convention(application.getCompileOptions());
                compile.setSource(source);
                compile.onlyIf("target is enabled",it ->
                    targets.zip(target,(list,item) -> list.isEmpty() || list.contains(item)).get()
                );
            });
            objectFiles.from(compileTask);

            final var commandsTask = tasks.register("compileCCommands",MetalCompileCommands.class,task ->
            {
                final var output = buildDirectory.file( task.getTarget().map("commands/main/c/%s/commands.json"::formatted) );
                task.getCompiler().convention(compileTask.flatMap(MetalCompile::getCompiler));
                task.getOptions().convention(compileTask.flatMap(MetalCompile::getInternalOptions));
                task.getCompileDirectory().convention(compileTask.map(it -> it.getTargetOutputDirectory().get().getAsFile()));
                task.setSource(source);
                task.getOutput().convention(output);
            });
            commandsElements.configure(it -> it.getOutgoing().artifact(commandsTask));
        });

        plugins.withPlugin("br.dev.pedrolamarao.metal.cxx",cxx ->
        {
            final var precompileTask = tasks.register("precompileIxx",MetalIxxPrecompile.class,precompile ->
            {
                final var target = precompile.getMetal().map(MetalService::getTarget);
                final var targets = application.getTargets();
                precompile.dependsOn(
                    includeDependencies.map(Configuration::getBuildDependencies),
                    importDependencies.map(Configuration::getBuildDependencies)
                );
                precompile.getImportPath().convention(importPath);
                precompile.getIncludePath().convention(includePath);
                precompile.getOutputDirectory().convention(buildDirectory.dir("bmi/main/ixx"));
                precompile.getOptions().convention(application.getCompileOptions());
                precompile.setSource(layout.getProjectDirectory().dir("src/main/ixx"));
                precompile.onlyIf("target is enabled",it ->
                    targets.zip(target,(list,item) -> list.isEmpty() || list.contains(item)).get()
                );
            });
            final var compileImports = precompileTask.zip(importPath,(precompile,dependencies) -> {
                final var list = new ArrayList<String>();
                list.add(precompile.getTargetOutputDirectory().get().toString());
                list.addAll(dependencies);
                return list;
            });

            final var precommandsTask = tasks.register("precompileIxxCommands",MetalCompileCommands.class,task ->
            {
                final var output = buildDirectory.file( task.getTarget().map("commands/main/ixx/%s/commands.json"::formatted) );
                task.getCompiler().convention(precompileTask.flatMap(MetalCompile::getCompiler));
                task.getOptions().convention(precompileTask.flatMap(MetalCompile::getInternalOptions));
                task.getCompileDirectory().convention(precompileTask.flatMap(it -> it.getOutputDirectory().getAsFile()));
                task.setSource(layout.getProjectDirectory().dir("src/main/ixx"));
                task.getOutput().convention(output);
            });
            commandsElements.configure(it -> it.getOutgoing().artifact(precommandsTask));

            final var compileSources = objects.fileCollection();
            compileSources.from(layout.getProjectDirectory().dir("src/main/cxx"));
            compileSources.from(precompileTask);

            final var compileTask = tasks.register("compileCxx",MetalCxxCompile.class,compile ->
            {
                final var target = compile.getMetal().map(MetalService::getTarget);
                final var targets = application.getTargets();
                compile.getImportPath().convention(compileImports);
                compile.getIncludePath().convention(includePath);
                compile.getOutputDirectory().convention(buildDirectory.dir("obj/main/cxx"));
                compile.getOptions().convention(application.getCompileOptions());
                compile.setSource(compileSources);
                compile.onlyIf("target is enabled",it ->
                    targets.zip(target,(list,item) -> list.isEmpty() || list.contains(item)).get()
                );
            });
            objectFiles.from(compileTask);

            final var commandsTask = tasks.register("compileCxxCommands",MetalCompileCommands.class,task ->
            {
                final var output = buildDirectory.file( task.getTarget().map("commands/main/cxx/%s/commands.json"::formatted) );
                task.getCompiler().convention(compileTask.flatMap(MetalCompile::getCompiler));
                task.getOptions().convention(compileTask.flatMap(MetalCompile::getInternalOptions));
                task.getCompileDirectory().convention(compileTask.flatMap(it -> it.getOutputDirectory().getAsFile()));
                task.setSource(layout.getProjectDirectory().dir("src/main/cxx"));
                task.getOutput().convention(output);
            });
            commandsElements.configure(it -> it.getOutgoing().artifact(commandsTask));
        });

        final var linkTask = tasks.register("link",MetalLink.class,link ->
        {
            final var target = link.getMetal().map(MetalService::getTarget);
            final var applicationName = target.map(t -> Metal.executableFileName(t,name));
            final var applicationFile = layout.getBuildDirectory().zip(target,(dir,t) ->
                dir.file("exe/main/%s/%s".formatted(t,applicationName.get()))
            );
            link.dependsOn(linkDependencies.map(Configuration::getBuildDependencies));
            link.getLinkDependencies().from(linkDependencies);
            link.getOutput().convention(applicationFile);
            link.getOptions().convention(application.getLinkOptions());
            link.setSource(objectFiles);
        });

        tasks.register("run",Exec.class,exec ->
        {
            final var executable = linkTask.flatMap(MetalLink::getOutput);
            exec.getInputs().file(executable);
            exec.setExecutable(executable.get());
            exec.onlyIf("executable file exists",spec ->
                Files.exists(executable.get().getAsFile().toPath())
            );
        });

        tasks.named("assemble",it -> {
            it.dependsOn(linkTask);
        });
    }
}
