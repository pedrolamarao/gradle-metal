package br.dev.pedrolamarao.gradle.metal.cxx;

import org.gradle.api.file.FileCollection;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.tasks.TaskProvider;

public class CxxSources
{
    final ListProperty<String> compileOptions;

    final TaskProvider<CxxCompileTask> compileTask;

    final TaskProvider<CxxCompileInterfaceTask> compileInterfaceTask;

    final FileCollection objects;

    final SourceDirectorySet sources;

    public CxxSources (ListProperty<String> compileOptions, TaskProvider<CxxCompileTask> compileTask, TaskProvider<CxxCompileInterfaceTask> compileInterfaceTask, FileCollection objects, SourceDirectorySet sources)
    {
        this.compileOptions = compileOptions;
        this.compileTask = compileTask;
        this.compileInterfaceTask = compileInterfaceTask;
        this.objects = objects;
        this.sources = sources;
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

    public FileCollection getObjects ()
    {
        return objects;
    }

    public SourceDirectorySet getSources ()
    {
        return sources;
    }
}
