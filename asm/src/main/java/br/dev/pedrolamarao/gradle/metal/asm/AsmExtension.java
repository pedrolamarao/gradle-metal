package br.dev.pedrolamarao.gradle.metal.asm;

import org.gradle.api.file.ProjectLayout;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.plugins.ExtensionAware;
import org.gradle.api.tasks.TaskContainer;

import javax.inject.Inject;

public abstract class AsmExtension implements ExtensionAware
{
    final ObjectFactory objects;

    final ProjectLayout layout;

    final TaskContainer tasks;

    @Inject
    public AsmExtension (ObjectFactory objects, ProjectLayout layout, TaskContainer tasks)
    {
        this.objects = objects;
        this.layout = layout;
        this.tasks = tasks;
    }

    public AsmSources create (String name)
    {
        final var sourceDirectory = layout.getProjectDirectory().dir("src/%s/asm".formatted(name));
        final var outputDirectory = layout.getBuildDirectory().dir("obj/%s/asm".formatted(name));

        final var sourceDirectorySet = objects.sourceDirectorySet(name, "%s assembler sources".formatted(name));
        sourceDirectorySet.srcDir(sourceDirectory);

        final var compileTask = tasks.register("compile%sassembler", AsmCompileTask.class, it -> {
            it.getOutputDirectory().set(outputDirectory);
            it.setSource(sourceDirectorySet);
        });

        return objects.newInstance(AsmSources.class,compileTask,sourceDirectorySet);
    }
}
