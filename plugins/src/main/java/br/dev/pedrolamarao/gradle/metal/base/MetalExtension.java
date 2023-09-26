// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal.base;

import org.gradle.api.Action;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.plugins.ExtensionAware;
import org.gradle.api.tasks.Exec;
import org.gradle.api.tasks.TaskContainer;

import javax.annotation.Nonnull;
import javax.inject.Inject;

public abstract class MetalExtension implements ExtensionAware
{
    final ConfigurationContainer configurations;

    final ObjectFactory objects;

    final TaskContainer tasks;

    @Inject
    public MetalExtension (ConfigurationContainer configurations, ObjectFactory objects, TaskContainer tasks)
    {
        this.configurations = configurations;
        this.objects = objects;
        this.tasks = tasks;
    }

    @Nonnull
    public MetalApplication application (String name)
    {
        final var linkOptions = objects.listProperty(String.class);

        final var linkTask = tasks.register("link-%s".formatted(name), NativeLinkTask.class, it ->
        {
            final var project = it.getProject();
            final var nativeLinkDependencies = project.getConfigurations().named("nativeLinkDependencies");
            final var output = project.getLayout().getBuildDirectory().file("exe/%s/%s.exe".formatted(name,project.getName()));
            it.getLibraryDependencies().from(nativeLinkDependencies);
            it.getOptions().convention(linkOptions);
            it.getOutput().set(output);
        });

        final var runTask = tasks.register("run-%s".formatted(name), Exec.class, it ->
        {
            it.executable(linkTask.get().getOutputs().getFiles().getSingleFile());
        });

        return new MetalApplication(linkOptions, linkTask, name);
    }

    @Nonnull
    public MetalApplication application (String name, Action<? super MetalApplication> configure)
    {
        final var sources = application(name);
        configure.execute(sources);
        return sources;
    }

    @Nonnull
    public MetalArchive archive (String name)
    {
        final var archiveOptions = objects.listProperty(String.class);

        final var archiveTask = tasks.register("archive-%s".formatted(name), NativeArchiveTask.class, it ->
        {
            final var project = it.getProject();
            final var output = project.getLayout().getBuildDirectory().file("lib/%s/%s.lib".formatted(name,project.getName()));
            it.getOptions().convention(archiveOptions);
            it.getOutput().set(output);
        });

        configurations.named("nativeLinkElements").configure(it -> {
            it.getOutgoing().artifact(archiveTask);
        });

        return new MetalArchive(archiveOptions, archiveTask);
    }

    @Nonnull
    public MetalArchive archive (String name, Action<? super MetalArchive> configure)
    {
        final var sources = archive(name);
        configure.execute(sources);
        return sources;
    }
}
