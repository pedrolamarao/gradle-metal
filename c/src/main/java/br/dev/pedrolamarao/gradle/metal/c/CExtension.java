// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal.c;

import org.gradle.api.Action;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.file.Directory;
import org.gradle.api.file.ProjectLayout;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.plugins.ExtensionAware;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.api.tasks.TaskContainer;

import javax.annotation.Nonnull;
import javax.inject.Inject;

public abstract class CExtension implements ExtensionAware
{
    final ConfigurationContainer configurations;

    final ObjectFactory objects;

    final ProjectLayout layout;

    final ProviderFactory providers;

    final TaskContainer tasks;

    @Inject
    public CExtension (ConfigurationContainer configurations, ObjectFactory objects, ProjectLayout layout, ProviderFactory providers, TaskContainer tasks)
    {
        this.configurations = configurations;
        this.objects = objects;
        this.layout = layout;
        this.providers = providers;
        this.tasks = tasks;
    }

    @Nonnull
    public CSources sources (String name)
    {
        final var sourceDirectory = layout.getProjectDirectory().dir("src/%s/c".formatted(name));
        final var objectDirectory = layout.getBuildDirectory().dir("obj/%s/c".formatted(name));

        final var sourceDirectorySet = objects.sourceDirectorySet(name, "%s c sources".formatted(name));
        sourceDirectorySet.srcDir(sourceDirectory);

        final var compileOptions = objects.listProperty(String.class);

        final var includeDependencies = objects.fileCollection();
        includeDependencies.from( configurations.named("cppIncludeDependencies") );

        final var compileTask = tasks.register("compile-%s-c".formatted(name), CCompileTask.class, it -> {
            it.getHeaderDependencies().from(includeDependencies);
            it.getCompileOptions().convention(compileOptions);
            it.getOutputDirectory().set(objectDirectory);
            it.setSource(sourceDirectorySet);
        });

        tasks.register("commands-%s-c".formatted(name), CCommandsTask.class, it -> {
            final var outputDirectory = layout.getBuildDirectory().dir("db/%s/c".formatted(name));
            it.getHeaderDependencies().from(includeDependencies);
            it.getCompileOptions().convention(compileOptions);
            it.getObjectDirectory().set(objectDirectory.map(Directory::getAsFile));
            it.getOutputDirectory().set(outputDirectory);
            it.setSource(sourceDirectorySet);
        });

        return new CSources(compileOptions, compileTask, includeDependencies, name, sourceDirectorySet);
    }

    @Nonnull
    public CSources sources (String name, Action<? super CSources> configure)
    {
        final var sources = sources(name);
        configure.execute(sources);
        return sources;
    }
}
