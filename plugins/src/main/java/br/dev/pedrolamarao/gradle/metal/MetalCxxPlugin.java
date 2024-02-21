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
        final var extensions = project.getExtensions();
        final var plugins = project.getPluginManager();

        plugins.withPlugin("br.dev.pedrolamarao.metal.application",plugin ->
        {
            final var application = (MetalApplicationImpl) extensions.getByType(MetalApplication.class);
            registerMain(project,application);
        });

        plugins.withPlugin("br.dev.pedrolamarao.metal.library",plugin ->
        {
            final var library = (MetalLibraryImpl) extensions.getByType(MetalLibrary.class);
            registerMain(project,library);

            final var test = (MetalApplicationImpl) extensions.getByType(MetalApplication.class);
            registerTest(project,test);
        });
    }

    private static void registerMain (Project project, MetalComponentImpl component)
    {
        final var configurations = project.getConfigurations();
        final var layout = project.getLayout();
        final var objects =  project.getObjects();
        final var tasks = project.getTasks();

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

        final var precompileTask = tasks.register("precompileIxx",MetalIxxPrecompile.class,task ->
        {
            final var condition = component.getTargets().zip(task.getTarget(),
                (allowed,target) -> allowed.isEmpty() || allowed.contains(target)
            );
            final var output = task.getProject().getLayout().getBuildDirectory().dir(
                task.getTarget().map("bmi/main/ixx/%s"::formatted)
            );
            final var source = task.getProject().getLayout().getProjectDirectory().dir("src/main/ixx");

            task.dependsOn(
                includeDependencies.map(Configuration::getBuildDependencies),
                importDependencies.map(Configuration::getBuildDependencies)
            );
            task.getImportPath().convention(importPath);
            task.getIncludePath().convention(includePath);
            task.getOutputDirectory().convention(output);
            task.getOptions().convention(component.getCompileOptions());
            task.setSource(source);
            task.getTarget().convention(component.getTarget());

            task.exclude(component.getExcludes());
            task.include(component.getIncludes());
            task.onlyIf("target is enabled",it -> condition.get());
        });
        importableElements.configure(it ->
            it.getOutgoing().artifact(precompileTask)
        );

        final var precommandsTask = tasks.register("precompileIxxCommands",MetalCompileCommands.class,task ->
        {
            final var output = task.getProject().getLayout().getBuildDirectory().file(
                task.getTarget().map("commands/main/ixx/%s/commands.json"::formatted)
            );

            task.getCompiler().convention(precompileTask.flatMap(MetalCompile::getCompiler));
            task.getOptions().convention(precompileTask.flatMap(MetalCompile::getInternalOptions));
            task.getCompileDirectory().convention(precompileTask.flatMap(it -> it.getOutputDirectory().getAsFile()));
            task.setSource(projectDirectory.dir("src/main/ixx"));
            task.getOutput().convention(output);
            task.getTarget().convention(component.getTarget());

            task.exclude(component.getExcludes());
            task.include(component.getIncludes());
        });
        component.getCommandFiles().from(precommandsTask);
        commandsElements.configure(it -> it.getOutgoing().artifact(precommandsTask));

        final var compileImports = precompileTask.zip(importPath,(precompile,dependencies) -> {
            final var list = new ArrayList<String>();
            list.add(precompile.getOutputDirectory().get().toString());
            list.addAll(dependencies);
            return list;
        });

        final var compileSources = objects.fileCollection();
        compileSources.from(layout.getProjectDirectory().dir("src/main/cxx"));
        compileSources.from(precompileTask);

        final var compileTask = tasks.register("compileCxx",MetalCxxCompile.class,task ->
        {
            final var condition = component.getTargets().zip(task.getTarget(),
                (allowed,target) -> allowed.isEmpty() || allowed.contains(target)
            );
            final var output = task.getProject().getLayout().getBuildDirectory().dir(
                task.getTarget().map("obj/main/cxx/%s"::formatted)
            );

            task.getImportPath().convention(compileImports);
            task.getIncludePath().convention(includePath);
            task.getOutputDirectory().convention(output);
            task.getOptions().convention(component.getCompileOptions());
            task.setSource(compileSources);
            task.getTarget().convention(component.getTarget());

            task.exclude(component.getExcludes());
            task.include(component.getIncludes());
            task.onlyIf("target is enabled",it -> condition.get());
        });
        component.getObjectFiles().from(compileTask);

        final var commandsTask = tasks.register("compileCxxCommands",MetalCompileCommands.class,task ->
        {
            final var output = task.getProject().getLayout().getBuildDirectory().file(
                task.getTarget().map("commands/main/cxx/%s/commands.json"::formatted)
            );

            task.getCompiler().convention(compileTask.flatMap(MetalCompile::getCompiler));
            task.getOptions().convention(compileTask.flatMap(MetalCompile::getInternalOptions));
            task.getOutput().convention(output);
            task.getCompileDirectory().convention(compileTask.flatMap(it -> it.getOutputDirectory().getAsFile()));
            task.setSource(compileSources);
            task.getTarget().convention(component.getTarget());

            task.exclude(component.getExcludes());
            task.include(component.getIncludes());
        });
        component.getCommandFiles().from(commandsTask);
        commandsElements.configure(it -> it.getOutgoing().artifact(commandsTask));
    }

    private static void registerTest (Project project, MetalComponentImpl component)
    {
        final var configurations = project.getConfigurations();
        final var layout = project.getLayout();
        final var objects =  project.getObjects();
        final var tasks = project.getTasks();

        final var projectDirectory = layout.getProjectDirectory();

        final var commandsElements = configurations.named(Metal.COMMANDS_ELEMENTS);
        final var importDependencies = configurations.named("testImportDependencies");
        final var includeDependencies = configurations.named("testIncludeDependencies");

        final var importPath = importDependencies.map(it -> {
            final var list = new HashSet<String>();
            list.add(tasks.named("precompileIxx",MetalIxxPrecompile.class).get().getOutputDirectory().get().toString());
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

        final var precompileTask = tasks.register("precompileTestIxx",MetalIxxPrecompile.class,task ->
        {
            final var condition = component.getTargets().zip(task.getTarget(),
                (allowed,target) -> allowed.isEmpty() || allowed.contains(target)
            );
            final var output = task.getProject().getLayout().getBuildDirectory().dir(
                task.getTarget().map("bmi/test/ixx/%s"::formatted)
            );
            final var source = task.getProject().getLayout().getProjectDirectory().dir("src/test/ixx");

            task.dependsOn(
                includeDependencies.map(Configuration::getBuildDependencies),
                importDependencies.map(Configuration::getBuildDependencies)
            );
            task.getImportPath().convention(importPath);
            task.getIncludePath().convention(includePath);
            task.getOutputDirectory().convention(output);
            task.getOptions().convention(component.getCompileOptions());
            task.setSource(source);
            task.getTarget().convention(component.getTarget());

            task.exclude(component.getExcludes());
            task.include(component.getIncludes());
            task.onlyIf("target is enabled",it -> condition.get());
        });

        final var precommandsTask = tasks.register("precompileTestIxxCommands",MetalCompileCommands.class,task ->
        {
            final var output = task.getProject().getLayout().getBuildDirectory().file(
                task.getTarget().map("commands/test/ixx/%s/commands.json"::formatted)
            );

            task.getCompiler().convention(precompileTask.flatMap(MetalCompile::getCompiler));
            task.getOptions().convention(precompileTask.flatMap(MetalCompile::getInternalOptions));
            task.getCompileDirectory().convention(precompileTask.flatMap(it -> it.getOutputDirectory().getAsFile()));
            task.setSource(projectDirectory.dir("src/test/ixx"));
            task.getOutput().convention(output);
            task.getTarget().convention(component.getTarget());

            task.exclude(component.getExcludes());
            task.include(component.getIncludes());
        });
        component.getCommandFiles().from(precommandsTask);
        commandsElements.configure(it -> it.getOutgoing().artifact(precommandsTask));

        final var compileImports = precompileTask.zip(importPath,(precompile,dependencies) -> {
            final var list = new ArrayList<String>();
            list.add(tasks.named("precompileIxx",MetalIxxPrecompile.class).get().getOutputDirectory().get().toString());
            list.add(precompile.getOutputDirectory().get().toString());
            list.addAll(dependencies);
            return list;
        });

        final var compileSources = objects.fileCollection();
        compileSources.from(layout.getProjectDirectory().dir("src/test/cxx"));
        compileSources.from(precompileTask);

        final var compileTask = tasks.register("compileTestCxx",MetalCxxCompile.class,task ->
        {
            final var condition = component.getTargets().zip(task.getTarget(),
                (allowed,target) -> allowed.isEmpty() || allowed.contains(target)
            );
            final var output = task.getProject().getLayout().getBuildDirectory().dir(
                task.getTarget().map("obj/test/cxx/%s"::formatted)
            );

            task.dependsOn(tasks.named("precompileIxx")); // TODO
            task.getImportPath().convention(compileImports);
            task.getIncludePath().convention(includePath);
            task.getOutputDirectory().convention(output);
            task.getOptions().convention(component.getCompileOptions());
            task.setSource(compileSources);
            task.getTarget().convention(component.getTarget());

            task.exclude(component.getExcludes());
            task.include(component.getIncludes());
            task.onlyIf("target is enabled",it -> condition.get());
        });
        component.getObjectFiles().from(compileTask);

        final var commandsTask = tasks.register("compileTestCxxCommands",MetalCompileCommands.class,task ->
        {
            final var output = task.getProject().getLayout().getBuildDirectory().file(
                task.getTarget().map("commands/test/cxx/%s/commands.json"::formatted)
            );

            task.getCompiler().convention(compileTask.flatMap(MetalCompile::getCompiler));
            task.getOptions().convention(compileTask.flatMap(MetalCompile::getInternalOptions));
            task.getCompileDirectory().convention(compileTask.flatMap(it -> it.getOutputDirectory().getAsFile()));
            task.setSource(compileSources);
            task.getOutput().convention(output);
            task.getTarget().convention(component.getTarget());

            task.exclude(component.getExcludes());
            task.include(component.getIncludes());
        });
        component.getCommandFiles().from(commandsTask);
        commandsElements.configure(it -> it.getOutgoing().artifact(commandsTask));
    }
}
