// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal.cxx;

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

public abstract class CxxExtension implements ExtensionAware
{
    final ConfigurationContainer configurations;

    final ObjectFactory objects;

    final ProjectLayout layout;

    final ProviderFactory providers;

    final TaskContainer tasks;

    @Inject
    public CxxExtension (ConfigurationContainer configurations, ObjectFactory objects, ProjectLayout layout, ProviderFactory providers, TaskContainer tasks)
    {
        this.configurations = configurations;
        this.objects = objects;
        this.layout = layout;
        this.providers = providers;
        this.tasks = tasks;
    }

    @Nonnull
    public CxxSources sources (String name)
    {
        final var options = objects.listProperty(String.class);

        final var sourceDirectory = layout.getProjectDirectory().dir("src/%s/cxx".formatted(name));
        final var sourceDirectorySet = objects.sourceDirectorySet(name, "%s c++ sources".formatted(name));
        sourceDirectorySet.srcDir(sourceDirectory);

        final var includeDependencies = configurations.findByName("cppIncludeDependencies");
        final var importDependencies = configurations.named("cxxImportDependencies");
        final var objectDirectory = layout.getBuildDirectory().dir("obj/%s/cxx".formatted(name));

        final var compileTask = tasks.register("compile-%s-cxx".formatted(name), CxxCompileTask.class, it ->
        {
            it.getCompileOptions().set(options);
            it.getHeaderDependencies().from(includeDependencies);
            it.getModuleDependencies().from(importDependencies);
            it.getOutputDirectory().set(objectDirectory);
            it.setSource(sourceDirectorySet);
        });

        tasks.register("commands-%s-cxx".formatted(name), CxxCommandsTask.class, it ->
        {
            final var outputDirectory = layout.getBuildDirectory().dir("db/%s/cxx".formatted(name));
            it.getCompileOptions().set(options);
            it.getHeaderDependencies().from(includeDependencies);
            it.getModuleDependencies().from(importDependencies);
            it.getObjectDirectory().set(objectDirectory.map(Directory::getAsFile));
            it.getOutputDirectory().set(outputDirectory);
            it.setSource(sourceDirectorySet);
        });

        return new CxxSources(options, compileTask, name, sourceDirectorySet);
    }

    @Nonnull
    public CxxSources sources (String name, Action<? super CxxSources> configure)
    {
        final var sources = sources(name);
        configure.execute(sources);
        return sources;
    }
}
