package br.dev.pedrolamarao.gradle.metal.cxx;

import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.tasks.TaskProvider;

public class CxxSources
{
    final ListProperty<String> compileOptions;

    final TaskProvider<CxxCompileTask> compileTask;

    final TaskProvider<CxxCompileInterfaceTask> compileInterfaceTask;

    final SourceDirectorySet sourceDirectorySet;

    public CxxSources (ListProperty<String> compileOptions, TaskProvider<CxxCompileTask> compileTask, TaskProvider<CxxCompileInterfaceTask> compileInterfaceTask, SourceDirectorySet sourceDirectorySet)
    {
        this.compileTask = compileTask;
        this.compileInterfaceTask = compileInterfaceTask;
        this.sourceDirectorySet = sourceDirectorySet;
        this.compileOptions = compileOptions;
    }

    public ListProperty<String> getCompileOptions () { return compileOptions; }

    public TaskProvider<CxxCompileTask> getCompileTask ()
    {
        return compileTask;
    }

    public TaskProvider<CxxCompileInterfaceTask> getCompileInterfaceTask ()
    {
        return compileInterfaceTask;
    }

    public SourceDirectorySet getSourceDirectories ()
    {
        return sourceDirectorySet;
    }
}
