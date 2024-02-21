// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Gradle Metal C++ language plugin.
 */
public class MetalCxxPlugin implements Plugin<Project>
{
    @Override
    public void apply (Project project)
    {
        project.getPluginManager().withPlugin("br.dev.pedrolamarao.metal.application",plugin ->
        {
            final var application = project.getExtensions().getByType(MetalApplication.class);
            registerMain(project,application);
        });

        project.getPluginManager().withPlugin("br.dev.pedrolamarao.metal.library",plugin ->
        {
            final var library = project.getExtensions().getByType(MetalLibrary.class);
            registerMain(project,library);
            registerTest(project,library);
        });
    }

    static void registerMain (Project project, MetalComponent component)
    {
        final var configurations = project.getConfigurations();
        final var layout = project.getLayout();
        final var objects =  project.getObjects();
        final var tasks = project.getTasks();

        final var buildDirectory = layout.getBuildDirectory();
        final var projectDirectory = layout.getProjectDirectory();

        final var commandsElements = configurations.named(Metal.COMMANDS_ELEMENTS);
        final var importableElements = configurations.named(Metal.IMPORTABLE_ELEMENTS);

        final var importDependencies = configurations.named(Metal.IMPORTABLE_DEPENDENCIES);
        final var includeDependencies = configurations.named(Metal.INCLUDABLE_DEPENDENCIES);

        final var importPath = importDependencies.map(it -> {
            final var list = new HashSet<String>();
            it.getElements().get().forEach(element -> list.add(element.toString()));
            return list;
        });
        final var includePath = includeDependencies.map(it -> {
            final var list = new HashSet<String>();
            list.add(projectDirectory.dir("src/main/cpp").toString());
            it.getElements().get().forEach(element -> list.add(element.toString()));
            return list;
        });

        final var precompileTask = tasks.register("precompileIxx",MetalIxxPrecompile.class,precompile ->
        {
            final var target = precompile.getTarget();
            final var targets = component.getTargets();
            precompile.dependsOn(
                includeDependencies.map(Configuration::getBuildDependencies),
                importDependencies.map(Configuration::getBuildDependencies)
            );
            precompile.getImportPath().convention(importPath);
            precompile.getIncludePath().convention(includePath);
            precompile.getOutputDirectory().convention(buildDirectory.dir("bmi/main/ixx"));
            precompile.getOptions().convention(component.getCompileOptions());
            precompile.setSource(layout.getProjectDirectory().dir("src/main/ixx"));
            precompile.onlyIf("target is enabled",it ->
                targets.zip(target,(list,item) -> list.isEmpty() || list.contains(item)).get()
            );
        });
        importableElements.configure(it ->
            it.getOutgoing().artifact(precompileTask.map(MetalIxxPrecompile::getTargetOutputDirectory),it2 ->
                it2.builtBy(precompileTask)
            )
        );

        final var precommandsTask = tasks.register("precompileIxxCommands",MetalCompileCommands.class,task ->
        {
            final var output = buildDirectory.file( task.getTarget().map("commands/main/ixx/%s/commands.json"::formatted) );
            task.getCompiler().convention(precompileTask.flatMap(MetalCompile::getCompiler));
            task.getOptions().convention(precompileTask.flatMap(MetalCompile::getInternalOptions));
            task.getCompileDirectory().convention(precompileTask.flatMap(it -> it.getOutputDirectory().getAsFile()));
            task.setSource(projectDirectory.dir("src/main/ixx"));
            task.getOutput().convention(output);
        });
        commandsElements.configure(it -> it.getOutgoing().artifact(precommandsTask));

        final var compileImports = precompileTask.zip(importPath,(precompile,dependencies) -> {
            final var list = new ArrayList<String>();
            list.add(precompile.getTargetOutputDirectory().get().toString());
            list.addAll(dependencies);
            return list;
        });

        final var compileSources = objects.fileCollection();
        compileSources.from(layout.getProjectDirectory().dir("src/main/cxx"));
        compileSources.from(precompileTask);

        final var compileTask = tasks.register("compileCxx",MetalCxxCompile.class,compile ->
        {
            final var target = compile.getTarget();
            final var targets = component.getTargets();
            compile.getImportPath().convention(compileImports);
            compile.getIncludePath().convention(includePath);
            compile.getOutputDirectory().convention(buildDirectory.dir("obj/main/cxx"));
            compile.getOptions().convention(component.getCompileOptions());
            compile.setSource(compileSources);
            compile.onlyIf("target is enabled",it ->
                targets.zip(target,(list,item) -> list.isEmpty() || list.contains(item)).get()
            );
        });
        component.getObjectFiles().from(compileTask);

        final var commandsTask = tasks.register("compileCxxCommands",MetalCompileCommands.class,task ->
        {
            final var output = buildDirectory.file( task.getTarget().map("commands/main/cxx/%s/commands.json"::formatted) );
            task.getCompiler().convention(compileTask.flatMap(MetalCompile::getCompiler));
            task.getOptions().convention(compileTask.flatMap(MetalCompile::getInternalOptions));
            task.getCompileDirectory().convention(compileTask.flatMap(it -> it.getOutputDirectory().getAsFile()));
            task.setSource(compileSources);
            task.getOutput().convention(output);
        });
        commandsElements.configure(it -> it.getOutgoing().artifact(commandsTask));
    }

    static void registerTest (Project project, MetalComponent component)
    {
        final var configurations = project.getConfigurations();
        final var layout = project.getLayout();
        final var objects =  project.getObjects();
        final var tasks = project.getTasks();

        final var buildDirectory = layout.getBuildDirectory();
        final var projectDirectory = layout.getProjectDirectory();

        final var commandsElements = configurations.named(Metal.COMMANDS_ELEMENTS);
        final var importDependencies = configurations.named("testImportDependencies");
        final var includeDependencies = configurations.named("testIncludeDependencies");

        final var importPath = importDependencies.map(it -> {
            final var list = new HashSet<String>();
            list.add(tasks.named("precompileIxx",MetalIxxPrecompile.class).get().getTargetOutputDirectory().get().toString());
            it.getElements().get().forEach(element -> list.add(element.toString()));
            return list;
        });
        final var includePath = includeDependencies.map(it -> {
            final var list = new HashSet<String>();
            list.add(projectDirectory.dir("src/main/cpp").toString());
            list.add(projectDirectory.dir("src/test/cpp").toString());
            it.getElements().get().forEach(element -> list.add(element.toString()));
            return list;
        });

        final var precompileTask = tasks.register("precompileTestIxx",MetalIxxPrecompile.class,precompile ->
        {
            final var target = precompile.getTarget();
            final var targets = component.getTargets();
            precompile.dependsOn(
                includeDependencies.map(Configuration::getBuildDependencies),
                importDependencies.map(Configuration::getBuildDependencies)
            );
            precompile.getImportPath().convention(importPath);
            precompile.getIncludePath().convention(includePath);
            precompile.getOutputDirectory().convention(buildDirectory.dir("bmi/test/ixx"));
            precompile.getOptions().convention(component.getCompileOptions());
            precompile.setSource(layout.getProjectDirectory().dir("src/test/ixx"));
            precompile.onlyIf("target is enabled",it ->
                targets.zip(target,(list,item) -> list.isEmpty() || list.contains(item)).get()
            );
        });

        final var precommandsTask = tasks.register("precompileTestIxxCommands",MetalCompileCommands.class,task ->
        {
            final var output = buildDirectory.file( task.getTarget().map("commands/test/ixx/%s/commands.json"::formatted) );
            task.getCompiler().convention(precompileTask.flatMap(MetalCompile::getCompiler));
            task.getOptions().convention(precompileTask.flatMap(MetalCompile::getInternalOptions));
            task.getCompileDirectory().convention(precompileTask.flatMap(it -> it.getOutputDirectory().getAsFile()));
            task.setSource(projectDirectory.dir("src/test/ixx"));
            task.getOutput().convention(output);
        });
        commandsElements.configure(it -> it.getOutgoing().artifact(precommandsTask));

        final var compileImports = precompileTask.zip(importPath,(precompile,dependencies) -> {
            final var list = new ArrayList<String>();
            list.add(tasks.named("precompileIxx",MetalIxxPrecompile.class).get().getTargetOutputDirectory().get().toString());
            list.add(precompile.getTargetOutputDirectory().get().toString());
            list.addAll(dependencies);
            return list;
        });

        final var compileSources = objects.fileCollection();
        compileSources.from(layout.getProjectDirectory().dir("src/test/cxx"));
        compileSources.from(precompileTask);

        final var compileTask = tasks.register("compileTestCxx",MetalCxxCompile.class,compile ->
        {
            final var target = compile.getTarget();
            final var targets = component.getTargets();
            compile.dependsOn(tasks.named("precompileIxx")); // TODO
            compile.getImportPath().convention(compileImports);
            compile.getIncludePath().convention(includePath);
            compile.getOutputDirectory().convention(buildDirectory.dir("obj/test/cxx"));
            compile.getOptions().convention(component.getCompileOptions());
            compile.setSource(compileSources);
            compile.onlyIf("target is enabled",it ->
                targets.zip(target,(list,item) -> list.isEmpty() || list.contains(item)).get()
            );
        });
        component.getTestObjectFiles().from(compileTask);

        final var commandsTask = tasks.register("compileTestCxxCommands",MetalCompileCommands.class,task ->
        {
            final var output = buildDirectory.file( task.getTarget().map("commands/test/cxx/%s/commands.json"::formatted) );
            task.getCompiler().convention(compileTask.flatMap(MetalCompile::getCompiler));
            task.getOptions().convention(compileTask.flatMap(MetalCompile::getInternalOptions));
            task.getCompileDirectory().convention(compileTask.flatMap(it -> it.getOutputDirectory().getAsFile()));
            task.setSource(compileSources);
            task.getOutput().convention(output);
        });
        commandsElements.configure(it -> it.getOutgoing().artifact(commandsTask));
    }
}
