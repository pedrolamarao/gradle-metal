package br.dev.pedrolamarao.gradle.metal.asm;

import org.gradle.api.Action;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.file.Directory;
import org.gradle.api.file.ProjectLayout;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.plugins.ExtensionAware;
import org.gradle.api.tasks.TaskContainer;

import javax.annotation.Nonnull;
import javax.inject.Inject;

public abstract class AsmExtension implements ExtensionAware
{
    final ConfigurationContainer configurations;

    final ObjectFactory objects;

    final ProjectLayout layout;

    final TaskContainer tasks;

    @Inject
    public AsmExtension (ConfigurationContainer configurations, ObjectFactory objects, ProjectLayout layout, TaskContainer tasks)
    {
        this.configurations = configurations;
        this.objects = objects;
        this.layout = layout;
        this.tasks = tasks;
    }

    @Nonnull
    public AsmSources sources (String name)
    {
        final var sourceDirectory = layout.getProjectDirectory().dir("src/%s/asm".formatted(name));
        final var objectDirectory = layout.getBuildDirectory().dir("obj/%s/asm".formatted(name));

        final var sourceDirectorySet = objects.sourceDirectorySet(name, "%s assembler sources".formatted(name));
        sourceDirectorySet.srcDir(sourceDirectory);

        final var compileOptions = objects.listProperty(String.class);

        final var includeDependencies = objects.fileCollection();
        includeDependencies.from( configurations.named("cppIncludeDependencies") );

        final var compileTask = tasks.register("compile-%s-asm".formatted(name), AsmCompileTask.class, it ->
        {
            it.getCompileOptions().set(compileOptions);
            it.getHeaderDependencies().from(includeDependencies);
            it.getOutputDirectory().set(objectDirectory);
            it.setSource(sourceDirectorySet);
        });

        tasks.register("commands-%s-asm".formatted(name), AsmCommandsTask.class, it ->
        {
            final var outputDirectory = layout.getBuildDirectory().dir("db/%s/asm".formatted(name));
            it.getCompileOptions().set(compileOptions);
            it.getHeaderDependencies().from(includeDependencies);
            it.getObjectDirectory().set(objectDirectory.map(Directory::getAsFile));
            it.getOutputDirectory().set(outputDirectory);
            it.setSource(sourceDirectorySet);
        });

        return new AsmSources(compileTask, includeDependencies, name, sourceDirectorySet);
    }

    @Nonnull
    public AsmSources sources (String name, Action<? super AsmSources> configure)
    {
        final var sources = sources(name);
        configure.execute(sources);
        return sources;
    }
}
