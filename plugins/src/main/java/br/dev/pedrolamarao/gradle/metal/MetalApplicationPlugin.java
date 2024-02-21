// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.tasks.Exec;

import java.nio.file.Files;

/**
 * Gradle Metal application plugin.
 */
public class MetalApplicationPlugin implements Plugin<Project>
{
    @Override
    public void apply (Project project)
    {
        final var plugins = project.getPluginManager();
        final var tasks = project.getTasks();

        plugins.apply(BasePlugin.class);
        plugins.apply(MetalBasePlugin.class);

        final var application = project.getExtensions().create("application",MetalApplication.class);

        // tasks

        final var linkTask = tasks.register("link",MetalLink.class,link ->
        {
            final var projekt = link.getProject();

            final var buildDirectory = projekt.getLayout().getBuildDirectory();
            final var linkDependencies = projekt.getConfigurations().named(Metal.LINKABLE_DEPENDENCIES);
            final var projectName = projekt.getName();

            final var linkTarget = link.getTarget();
            final var linkName = linkTarget.map(target -> Metal.executableFileName(target,projectName));
            final var linkFile = buildDirectory.zip(linkTarget,(dir,target) ->
                dir.file("exe/main/%s/%s".formatted(target,linkName.get()))
            );

            link.dependsOn(linkDependencies.map(Configuration::getBuildDependencies));
            link.getLinkDependencies().from(linkDependencies);
            link.getOptions().convention(application.getLinkOptions());
            link.getOutput().convention(linkFile);
            link.setSource(application.getObjectFiles());
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

        tasks.named("assemble",assemble -> {
            assemble.dependsOn(linkTask);
        });
    }
}
