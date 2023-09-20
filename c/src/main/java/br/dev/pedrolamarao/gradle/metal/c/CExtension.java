package br.dev.pedrolamarao.gradle.metal.c;

import org.gradle.api.Action;
import org.gradle.api.artifacts.ConfigurationContainer;
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
        final var options = objects.listProperty(String.class);

        final var sourceDirectory = layout.getProjectDirectory().dir("src/%s/c".formatted(name));
        final var sourceDirectorySet = objects.sourceDirectorySet(name, "%s c sources".formatted(name));
        sourceDirectorySet.srcDir(sourceDirectory);

        final var includeDependencies = configurations.findByName("cppIncludeDependencies");

        final var objDirectory = layout.getBuildDirectory().dir("obj/%s/c".formatted(name));
        final var objTask = tasks.register("compile%scxx".formatted(name), CCompileTask.class, it -> {
            if (includeDependencies != null) it.getHeaderDependencies().from(includeDependencies);
            it.getCompileOptions().convention(options);
            it.getOutputDirectory().set(objDirectory);
            it.setSource(sourceDirectorySet);
        });

        return new CSources(options, objTask, sourceDirectorySet);
    }

    @Nonnull
    public CSources sources (String name, Action<? super CSources> configure)
    {
        final var sources = sources(name);
        configure.execute(sources);
        return sources;
    }
}
