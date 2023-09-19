package br.dev.pedrolamarao.gradle.metal.cpp;

import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.file.ProjectLayout;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.plugins.ExtensionAware;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.api.tasks.TaskContainer;

import javax.inject.Inject;

public abstract class CppExtension implements ExtensionAware
{
    final ConfigurationContainer configurations;

    final ObjectFactory objects;

    final ProjectLayout layout;

    final ProviderFactory providers;

    final TaskContainer tasks;

    @Inject
    public CppExtension (ConfigurationContainer configurations, ObjectFactory objects, ProjectLayout layout, ProviderFactory providers, TaskContainer tasks)
    {
        this.configurations = configurations;
        this.objects = objects;
        this.layout = layout;
        this.providers = providers;
        this.tasks = tasks;
    }

    public CppSources create (String name)
    {
        final var sourceDirectory = layout.getProjectDirectory().dir("src/%s/cpp".formatted(name));

        final var sourceDirectorySet = objects.sourceDirectorySet(name, "%s preprocessor sources".formatted(name));
        sourceDirectorySet.srcDir(sourceDirectory);

        configurations.named("cppIncludeElements").configure(it -> {
            it.getOutgoing().artifacts( providers.provider(sourceDirectorySet::getSourceDirectories) );
        });

        return objects.newInstance(CppSources.class,sourceDirectorySet);
    }
}