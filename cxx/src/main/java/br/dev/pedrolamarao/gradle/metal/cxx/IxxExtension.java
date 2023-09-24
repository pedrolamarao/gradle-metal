// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal.cxx;

import org.gradle.api.Action;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.file.ProjectLayout;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.plugins.ExtensionAware;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.api.tasks.TaskContainer;

import javax.annotation.Nonnull;
import javax.inject.Inject;

public abstract class IxxExtension implements ExtensionAware
{
    final ConfigurationContainer configurations;

    final ObjectFactory objects;

    final ProjectLayout layout;

    final ProviderFactory providers;

    final TaskContainer tasks;

    @Inject
    public IxxExtension (ConfigurationContainer configurations, ObjectFactory objects, ProjectLayout layout, ProviderFactory providers, TaskContainer tasks)
    {
        this.configurations = configurations;
        this.objects = objects;
        this.layout = layout;
        this.providers = providers;
        this.tasks = tasks;
    }

    @Nonnull
    public IxxSources sources (String name)
    {
        final var sourceDirectory = layout.getProjectDirectory().dir("src/%s/ixx".formatted(name));
        final var outputDirectory = layout.getBuildDirectory().dir("bmi/%s/ixx".formatted(name));

        final var sourceDirectorySet = objects.sourceDirectorySet(name, "%s c++ module interface sources".formatted(name));
        sourceDirectorySet.srcDir(sourceDirectory);

        final var compileOptions = objects.listProperty(String.class);

        final var importDependencies = objects.fileCollection();
        importDependencies.from( configurations.named("cxxImportDependencies") );

        final var includeDependencies = objects.fileCollection();
        includeDependencies.from( configurations.named("cppIncludeDependencies") );

        final var compileTask = tasks.register("compile-%s-ixx".formatted(name), IxxCompileTask.class, task ->
        {
            task.getCompileOptions().set(compileOptions);
            task.getHeaderDependencies().from(includeDependencies);
            task.getModuleDependencies().from(importDependencies);
            task.getOutputDirectory().set(outputDirectory);
            task.setSource(sourceDirectorySet);
        });

        configurations.named("cxxImportElements").configure(configuration -> {
            configuration.getOutgoing().artifacts(compileTask.map(IxxCompileTask::getInterfaceFiles), artifact -> {
                artifact.builtBy(compileTask);
            });
        });

        return new IxxSources(compileOptions, compileTask, importDependencies, includeDependencies, name);
    }

    @Nonnull
    public IxxSources sources (String name, Action<? super IxxSources> configure)
    {
        final var sources = sources(name);
        configure.execute(sources);
        return sources;
    }
}
