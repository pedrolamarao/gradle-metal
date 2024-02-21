// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.plugins.BasePlugin;
import org.gradle.api.tasks.Exec;

import java.nio.file.Files;

/**
 * Gradle Metal library project plugin.
 */
public class MetalLibraryPlugin implements Plugin<Project>
{
    @Override
    public void apply (Project project)
    {
        final var configurations = project.getConfigurations();
        final var layout = project.getLayout();
        final var plugins = project.getPluginManager();
        final var tasks = project.getTasks();

        plugins.apply(BasePlugin.class);
        plugins.apply(MetalBasePlugin.class);

        final var includableElements = configurations.named(Metal.INCLUDABLE_ELEMENTS);
        final var linkableElements = configurations.named(Metal.LINKABLE_ELEMENTS);

        final var library = (MetalLibraryImpl) project.getExtensions()
            .create(MetalLibrary.class,"library",MetalLibraryImpl.class);
        final var test = (MetalApplicationImpl) project.getExtensions()
            .create(MetalApplication.class,"test",MetalApplicationImpl.class);
        test.getCompileOptions().convention(library.getCompileOptions());

        final var includeDir = layout.getProjectDirectory().dir("src/main/cpp");
        includableElements.configure(it -> it.getOutgoing().artifact(includeDir));

        // tasks

        final var archiveTask = tasks.register("archive",MetalArchive.class,archive ->
        {
            final var projekt = archive.getProject();

            final var buildDirectory = projekt.getLayout().getBuildDirectory();
            final var projectName = projekt.getName();

            final var archiveTarget = archive.getTarget();
            final var archiveName = archiveTarget.map(t -> Metal.archiveFileName(t,projectName));
            final var archiveFile = buildDirectory.zip(archiveTarget,(dir,t) ->
                dir.file("lib/main/%s/%s".formatted(t,archiveName.get()))
            );

            archive.getOutput().convention(archiveFile);
            archive.setSource(library.getObjectFiles());
        });
        linkableElements.configure(it -> it.getOutgoing().artifact(archiveTask));

        final var linkTestTask = tasks.register("linkTest",MetalLink.class,link ->
        {
            final var projekt = link.getProject();

            final var buildDirectory = projekt.getLayout().getBuildDirectory();
            final var linkDependencies = configurations.named("testLinkDependencies");
            final var projectName = projekt.getName();

            final var linkTarget = link.getTarget();
            final var linkName = linkTarget.map(target -> Metal.executableFileName(target,projectName));
            final var linkFile = buildDirectory.zip(linkTarget,(dir,target) ->
                dir.file("exe/test/%s/%s".formatted(target,linkName.get()))
            );

            link.dependsOn(linkDependencies.map(Configuration::getBuildDependencies));
            link.getLinkDependencies().from(archiveTask);
            link.getLinkDependencies().from(linkDependencies);
            link.getOptions().convention(test.getLinkOptions());
            link.getOutput().convention(linkFile);
            link.setSource(test.getObjectFiles());
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
