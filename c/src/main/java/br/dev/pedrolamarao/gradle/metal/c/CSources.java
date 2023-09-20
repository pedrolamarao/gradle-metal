package br.dev.pedrolamarao.gradle.metal.c;

import org.gradle.api.file.FileCollection;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.tasks.TaskProvider;

public class CSources
{
    final ListProperty<String> compileOptions;

    final TaskProvider<CCompileTask> compileTask;

    final FileCollection objects;

    final SourceDirectorySet sources;

    public CSources (ListProperty<String> compileOptions, TaskProvider<CCompileTask> compileTask, FileCollection objects, SourceDirectorySet sources)
    {
        this.compileOptions = compileOptions;
        this.compileTask = compileTask;
        this.objects = objects;
        this.sources = sources;
    }

    public ListProperty<String> getCompileOptions () { return compileOptions; }

    public TaskProvider<CCompileTask> getCompileTask ()
    {
        return compileTask;
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
