package br.dev.pedrolamarao.gradle.cxx.language;

import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.tasks.TaskProvider;

import javax.inject.Inject;

public class CxxSources
{
    final TaskProvider<CxxCompileTask> compileTask;

    final TaskProvider<CxxCompileInterfaceTask> compileInterfaceTask;

    final SourceDirectorySet sourceDirectorySet;

    final CxxCompileOptions options;

    public CxxSources (TaskProvider<CxxCompileTask> compileTask, TaskProvider<CxxCompileInterfaceTask> compileInterfaceTask, SourceDirectorySet sourceDirectorySet, CxxCompileOptions options)
    {
        this.compileTask = compileTask;
        this.compileInterfaceTask = compileInterfaceTask;
        this.sourceDirectorySet = sourceDirectorySet;
        this.options = options;
    }

    public SourceDirectorySet getSourceDirectories ()
    {
        return sourceDirectorySet;
    }

    public TaskProvider<CxxCompileTask> getCompileTask ()
    {
        return compileTask;
    }

    public TaskProvider<CxxCompileInterfaceTask> getCompileInterfaceTask ()
    {
        return compileInterfaceTask;
    }

    public CxxCompileOptions getOptions () { return options; }
}
