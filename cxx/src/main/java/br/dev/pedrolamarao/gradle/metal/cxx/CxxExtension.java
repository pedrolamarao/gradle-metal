package br.dev.pedrolamarao.gradle.metal.cxx;

import org.gradle.api.Action;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.file.ProjectLayout;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.plugins.ExtensionAware;
import org.gradle.api.provider.ListProperty;
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
    public CxxSources create (String name)
    {
        final var options = objects.listProperty(String.class);

        final var cxxDirectory = layout.getProjectDirectory().dir("src/%s/cxx".formatted(name));
        final var cxxDirectorySet = objects.sourceDirectorySet(name, "%s c++ sources".formatted(name));
        cxxDirectorySet.srcDir(cxxDirectory);

        final var cxxmDirectory = layout.getProjectDirectory().dir("src/%s/cxxm".formatted(name));
        final var cxxmDirectorySet = objects.sourceDirectorySet(name, "%s c++ module interface sources".formatted(name));
        cxxmDirectorySet.srcDir(cxxmDirectory);

        final var includeDependencies = configurations.findByName("cppIncludeDependencies");

        final var importDependencies = configurations.named("cxxImportDependencies");

        final var bmiDirectory = layout.getBuildDirectory().dir("bmi/%s/cxx".formatted(name));
        final var bmiTask = tasks.register("compile%scxxinterface".formatted(name), CxxCompileInterfaceTask.class, it -> {
            it.getCompileOptions().convention(options);
            if (includeDependencies != null) it.getHeaderDependencies().from(includeDependencies);
            it.getModuleDependencies().from(importDependencies.get());
            it.getOutputDirectory().set(bmiDirectory);
            it.setSource(cxxmDirectorySet);
        });

        configurations.named("cxxImportElements").configure(configuration -> {
            configuration.getOutgoing().artifacts(bmiTask.map(CxxCompileInterfaceTask::getInterfaceFiles), artifact -> {
                artifact.builtBy(bmiTask);
            });
        });

        final var objDirectory = layout.getBuildDirectory().dir("obj/%s/cxx".formatted(name));
        final var objTask = tasks.register("compile%scxx".formatted(name), CxxCompileTask.class, it -> {
            it.getCompileOptions().convention(options);
            if (includeDependencies != null) it.getHeaderDependencies().from(includeDependencies);
            it.getModuleDependencies().from(importDependencies.get());
            it.getModuleDependencies().from(bmiTask.get().getOutputs().getFiles().getAsFileTree());
            it.getOutputDirectory().set(objDirectory);
            it.setSource(cxxDirectorySet.plus(bmiTask.get().getInterfaceFiles()));
        });

        return new CxxSources(options, objTask, bmiTask, cxxDirectorySet);
    }

    @Nonnull
    public CxxSources create (String name, Action<? super CxxSources> configure)
    {
        final var sources = create(name);
        configure.execute(sources);
        return sources;
    }
}
