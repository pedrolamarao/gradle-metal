package br.dev.pedrolamarao.gradle.metal.c;

import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.tasks.TaskProvider;

public class CSources
{
    final ListProperty<String> compileOptions;

    final TaskProvider<CCompileTask> compileTask;

    final SourceDirectorySet sourceDirectorySet;

    public CSources (ListProperty<String> compileOptions, TaskProvider<CCompileTask> compileTask, SourceDirectorySet sourceDirectorySet)
    {
        this.compileOptions = compileOptions;
        this.compileTask = compileTask;
        this.sourceDirectorySet = sourceDirectorySet;
    }

    public ListProperty<String> getCompileOptions () { return compileOptions; }

    public TaskProvider<CCompileTask> getCompileTask ()
    {
        return compileTask;
    }

    public SourceDirectorySet getSourceDirectories ()
    {
        return sourceDirectorySet;
    }
}
