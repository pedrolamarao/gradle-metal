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

        final var commandsElements = configurations.named(Metal.COMMANDS_ELEMENTS);
        final var includeDependencies = configurations.named(Metal.INCLUDABLE_DEPENDENCIES);
        final var sourceDirectory = layout.getProjectDirectory().dir("src/main/c");

        final var includePath = includeDependencies.map(it -> {
            final var list = new HashSet<String>();
            list.add(layout.getProjectDirectory().dir("src/main/cpp").toString());
            it.getElements().get().forEach(element -> list.add(element.toString()));
            return list;
        });

        final var compileTask = tasks.register("compileC",MetalCCompile.class,task ->
        {
            final var condition = component.getTargets().zip(task.getTarget(),
                (allowed,target) -> allowed.isEmpty() || allowed.contains(target)
            );
            final var output = task.getProject().getLayout().getBuildDirectory().dir(
                task.getTarget().map("obj/main/c/%s"::formatted)
            );

            task.dependsOn(includeDependencies.map(Configuration::getBuildDependencies));
            task.getIncludePath().convention(includePath);
            task.getOutputDirectory().convention(output);
            task.getOptions().convention(component.getCompileOptions());
            task.setSource(sourceDirectory);
            task.getTarget().convention(component.getTarget());

            task.exclude(component.getExcludes());
            task.include(component.getIncludes());
            task.onlyIf("target is enabled",it -> condition.get());
        });
        component.getObjectFiles().from(compileTask);

        final var commandsTask = tasks.register("compileCCommands",MetalCompileCommands.class,task ->
        {
            final var output = task.getProject().getLayout().getBuildDirectory().file(
                task.getTarget().map("commands/main/c/%s/commands.json"::formatted)
            );

            task.getCompileCommand().convention(compileTask.flatMap(MetalCompileImpl::getCommand));
            task.getCompileDirectory().convention(compileTask.flatMap(it -> it.getOutputDirectory().getAsFile()));
            task.getDirectory().convention(task.getProject().getProjectDir());
            task.getOutput().convention(output);
            task.setSource(sourceDirectory);
            task.getTarget().convention(component.getTarget());

            task.exclude(component.getExcludes());
            task.include(component.getIncludes());
        });
        component.getCommandFiles().from(commandsTask).builtBy(commandsTask);
        commandsElements.configure(it -> it.getOutgoing().artifact(commandsTask));
    }

    private static void registerTest (Project project, MetalComponentImpl component)
    {
        final var configurations = project.getConfigurations();
        final var layout = project.getLayout();
        final var tasks = project.getTasks();

        final var commandsElements = configurations.named(Metal.COMMANDS_ELEMENTS);
        final var includeDependencies = configurations.named("testIncludeDependencies");
        final var sourceDirectory = layout.getProjectDirectory().dir("src/test/c");

        final var includePath = includeDependencies.map(it -> {
            final var list = new HashSet<String>();
            list.add(layout.getProjectDirectory().dir("src/main/cpp").toString());
            list.add(layout.getProjectDirectory().dir("src/test/cpp").toString());
            it.getElements().get().forEach(element -> list.add(element.toString()));
            return list;
        });

        final var compileTask = tasks.register("compileTestC",MetalCCompile.class,task ->
        {
            final var condition = component.getTargets().zip(task.getTarget(),
                (allowed,target) -> allowed.isEmpty() || allowed.contains(target)
            );
            final var output = task.getProject().getLayout().getBuildDirectory().dir(
                task.getTarget().map("obj/test/c/%s"::formatted)
            );

            task.dependsOn(includeDependencies.map(Configuration::getBuildDependencies));
            task.getIncludePath().convention(includePath);
            task.getOutputDirectory().convention(output);
            task.getOptions().convention(component.getCompileOptions());
            task.setSource(sourceDirectory);
            task.getTarget().convention(component.getTarget());

            task.exclude(component.getExcludes());
            task.include(component.getIncludes());
            task.onlyIf("target is enabled",it -> condition.get());
        });
        component.getObjectFiles().from(compileTask);

        final var commandsTask = tasks.register("compileTestCCommands",MetalCompileCommands.class,task ->
        {
            final var output = task.getProject().getLayout().getBuildDirectory().file(
                task.getTarget().map("commands/test/c/%s/commands.json"::formatted)
            );

            task.getCompileCommand().convention(compileTask.flatMap(MetalCompileImpl::getCommand));
            task.getCompileDirectory().convention(compileTask.flatMap(it -> it.getOutputDirectory().getAsFile()));
            task.getDirectory().convention(task.getProject().getProjectDir());
            task.getOutput().convention(output);
            task.setSource(sourceDirectory);
            task.getTarget().convention(component.getTarget());

            task.exclude(component.getExcludes());
            task.include(component.getIncludes());
        });
        component.getCommandFiles().from(commandsTask).builtBy(commandsTask);
        commandsElements.configure(it -> it.getOutgoing().artifact(commandsTask));
    }
}
