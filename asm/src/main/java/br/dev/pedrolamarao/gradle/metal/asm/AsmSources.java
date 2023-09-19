package br.dev.pedrolamarao.gradle.metal.asm;

import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.tasks.TaskProvider;

import javax.inject.Inject;

public abstract class AsmSources
{
    final TaskProvider<AsmCompileTask> compileTask;

    final SourceDirectorySet sourceDirectorySet;

    @Inject
    public AsmSources (TaskProvider<AsmCompileTask> compileTask, SourceDirectorySet sourceDirectorySet)
    {
        this.compileTask = compileTask;
        this.sourceDirectorySet = sourceDirectorySet;
    }

    public SourceDirectorySet getSourceDirectories ()
    {
        return sourceDirectorySet;
    }

    public TaskProvider<AsmCompileTask> getCompileTask ()
    {
        return compileTask;
    }
}
