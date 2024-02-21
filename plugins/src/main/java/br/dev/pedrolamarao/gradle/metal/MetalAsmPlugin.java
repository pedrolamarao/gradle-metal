// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * Gradle Metal Assembler language plugin.
 */
public class MetalAsmPlugin implements Plugin<Project>
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
        final var sourceDirectory = layout.getProjectDirectory().dir("src/main/asm");

        final var commandsElements = configurations.named(Metal.COMMANDS_ELEMENTS);

        final var compileTask = tasks.register("compileAsm",MetalAsmCompile.class,task ->
        {
            final var target = task.getTarget();
            final var targets = component.getTargets();
            task.getOutputDirectory().set(buildDirectory.dir("obj/main/asm"));
            task.getOptions().convention(component.getCompileOptions());
            task.setSource(sourceDirectory);
            task.onlyIf("target is enabled",it ->
                targets.zip(target,(list,item) -> list.isEmpty() || list.contains(item)).get()
            );
        });
        component.getObjectFiles().from(compileTask);

        final var commandsTask = tasks.register("compileAsmCommands",MetalCompileCommands.class,task ->
        {
            final var output = buildDirectory.file( task.getTarget().map("commands/main/asm/%s/commands.json"::formatted) );
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
        final var sourceDirectory = layout.getProjectDirectory().dir("src/test/asm");

        final var commandsElements = configurations.named(Metal.COMMANDS_ELEMENTS);

        final var compileTask = tasks.register("compileTestAsm",MetalAsmCompile.class,task ->
        {
            final var target = task.getTarget();
            final var targets = component.getTargets();
            task.getOutputDirectory().set(buildDirectory.dir("obj/test/asm"));
            task.getOptions().convention(component.getCompileOptions());
            task.setSource(sourceDirectory);
            task.onlyIf("target is enabled",it ->
                targets.zip(target,(list,item) -> list.isEmpty() || list.contains(item)).get()
            );
        });
        component.getObjectFiles().from(compileTask);

        final var commandsTask = tasks.register("compileTestAsmCommands",MetalCompileCommands.class,task ->
        {
            final var output = buildDirectory.file( task.getTarget().map("commands/test/asm/%s/commands.json"::formatted) );
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
