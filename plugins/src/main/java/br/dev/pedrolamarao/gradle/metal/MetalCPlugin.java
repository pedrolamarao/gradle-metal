// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;

import java.util.HashSet;

/**
 * Gradle Metal C language plugin.
 */
public class MetalCPlugin implements Plugin<Project>
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
        final var tasks = project.getTasks();

        final var buildDirectory = layout.getBuildDirectory();
        final var sourceDirectory = layout.getProjectDirectory().dir("src/main/c");

        final var commandsElements = configurations.named(Metal.COMMANDS_ELEMENTS);
        final var includeDependencies = configurations.named(Metal.INCLUDABLE_DEPENDENCIES);

        final var includePath = includeDependencies.map(it -> {
            final var list = new HashSet<String>();
            list.add(layout.getProjectDirectory().dir("src/main/cpp").toString());
            it.getElements().get().forEach(element -> list.add(element.toString()));
            return list;
        });

        final var compileTask = tasks.register("compileC",MetalCCompile.class,compile ->
        {
            final var target = compile.getMetal().map(MetalService::getTarget);
            final var targets = component.getTargets();
            compile.dependsOn(includeDependencies.map(Configuration::getBuildDependencies));
            compile.getIncludePath().convention(includePath);
            compile.getOutputDirectory().convention(buildDirectory.dir("obj/main/c"));
            compile.getOptions().convention(component.getCompileOptions());
            compile.setSource(sourceDirectory);
            compile.onlyIf("target is enabled",it ->
                targets.zip(target,(list,item) -> list.isEmpty() || list.contains(item)).get()
            );
        });
        component.getObjectFiles().from(compileTask);

        final var commandsTask = tasks.register("compileCCommands",MetalCompileCommands.class,task ->
        {
            final var output = buildDirectory.file( task.getTarget().map("commands/main/c/%s/commands.json"::formatted) );
            task.getCompiler().convention(compileTask.flatMap(MetalCompile::getCompiler));
            task.getOptions().convention(compileTask.flatMap(MetalCompile::getInternalOptions));
            task.getCompileDirectory().convention(compileTask.map(it -> it.getTargetOutputDirectory().get().getAsFile()));
            task.setSource(sourceDirectory);
            task.getOutput().convention(output);
        });
        component.getCommandFiles().from(commandsTask).builtBy(commandsTask);
        commandsElements.configure(it -> it.getOutgoing().artifact(commandsTask));
    }

    private static void registerTest (Project project, MetalComponentImpl component)
    {
        final var configurations = project.getConfigurations();
        final var layout = project.getLayout();
        final var tasks = project.getTasks();

        final var buildDirectory = layout.getBuildDirectory();
        final var sourceDirectory = layout.getProjectDirectory().dir("src/test/c");

        final var commandsElements = configurations.named(Metal.COMMANDS_ELEMENTS);
        final var includeDependencies = configurations.named("testIncludeDependencies");

        final var includePath = includeDependencies.map(it -> {
            final var list = new HashSet<String>();
            list.add(layout.getProjectDirectory().dir("src/main/cpp").toString());
            list.add(layout.getProjectDirectory().dir("src/test/cpp").toString());
            it.getElements().get().forEach(element -> list.add(element.toString()));
            return list;
        });

        final var compileTask = tasks.register("compileTestC",MetalCCompile.class,compile ->
        {
            final var target = compile.getMetal().map(MetalService::getTarget);
            final var targets = component.getTargets();
            compile.dependsOn(includeDependencies.map(Configuration::getBuildDependencies));
            compile.getIncludePath().convention(includePath);
            compile.getOutputDirectory().convention(buildDirectory.dir("obj/test/c"));
            compile.getOptions().convention(component.getCompileOptions());
            compile.setSource(sourceDirectory);
            compile.onlyIf("target is enabled",it ->
                targets.zip(target,(list,item) -> list.isEmpty() || list.contains(item)).get()
            );
        });
        component.getObjectFiles().from(compileTask);

        final var commandsTask = tasks.register("compileTestCCommands",MetalCompileCommands.class,task ->
        {
            final var output = buildDirectory.file( task.getTarget().map("commands/test/c/%s/commands.json"::formatted) );
            task.getCompiler().convention(compileTask.flatMap(MetalCompile::getCompiler));
            task.getOptions().convention(compileTask.flatMap(MetalCompile::getInternalOptions));
            task.getCompileDirectory().convention(compileTask.map(it -> it.getTargetOutputDirectory().get().getAsFile()));
            task.setSource(sourceDirectory);
            task.getOutput().convention(output);
        });
        component.getCommandFiles().from(commandsTask).builtBy(commandsTask);
        commandsElements.configure(it -> it.getOutgoing().artifact(commandsTask));
    }
}
