package br.dev.pedrolamarao.gradle.metal.c;

import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.tasks.TaskProvider;

public class CSources
{
    final TaskProvider<CCompileTask> compileTask;

    final SourceDirectorySet sourceDirectorySet;

    final CCompileOptions options;

    public CSources (TaskProvider<CCompileTask> compileTask, SourceDirectorySet sourceDirectorySet, CCompileOptions options)
    {
        this.compileTask = compileTask;
        this.sourceDirectorySet = sourceDirectorySet;
        this.options = options;
    }

    public SourceDirectorySet getSourceDirectories ()
    {
        return sourceDirectorySet;
    }

    public TaskProvider<CCompileTask> getCompileTask ()
    {
        return compileTask;
    }

    public CCompileOptions getOptions () { return options; }
}
