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
        project.getPluginManager().withPlugin("br.dev.pedrolamarao.metal.application",plugin ->
        {
            final var application = project.getExtensions().getByType(MetalApplication.class);
            MetalAsmPlugin.registerMain(project,application);
        });

        project.getPluginManager().withPlugin("br.dev.pedrolamarao.metal.library",plugin ->
        {
            final var library = project.getExtensions().getByType(MetalLibrary.class);
            MetalAsmPlugin.registerMain(project,library);
            MetalAsmPlugin.registerTest(project,library);
        });
    }

    static void registerMain (Project project, MetalComponent component)
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
        commandsElements.configure(it -> it.getOutgoing().artifact(commandsTask));
    }

    static void registerTest (Project project, MetalComponent component)
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
        component.getTestObjectFiles().from(compileTask);

        final var commandsTask = tasks.register("compileTestAsmCommands",MetalCompileCommands.class,task ->
        {
            final var output = buildDirectory.file( task.getTarget().map("commands/test/asm/%s/commands.json"::formatted) );
            task.getCompiler().convention(compileTask.flatMap(MetalCompile::getCompiler));
            task.getOptions().convention(compileTask.flatMap(MetalCompile::getInternalOptions));
            task.getCompileDirectory().convention(compileTask.map(it -> it.getTargetOutputDirectory().get().getAsFile()));
            task.setSource(sourceDirectory);
            task.getOutput().convention(output);
        });
        commandsElements.configure(it -> it.getOutgoing().artifact(commandsTask));
    }
}
