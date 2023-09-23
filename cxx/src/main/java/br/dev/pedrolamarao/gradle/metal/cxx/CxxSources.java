// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal.cxx;

import org.gradle.api.Named;
import org.gradle.api.NonNullApi;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.tasks.TaskProvider;

@NonNullApi
public class CxxSources implements Named
{
    final ListProperty<String> compileOptions;

    final TaskProvider<CxxCompileTask> compileTask;

    final String name;

    final SourceDirectorySet sources;

    public CxxSources (ListProperty<String> compileOptions, TaskProvider<CxxCompileTask> compileTask, String name, SourceDirectorySet sources)
    {
        this.compileOptions = compileOptions;
        this.compileTask = compileTask;
        this.name = name;
        this.sources = sources;
    }

    public ListProperty<String> getCompileOptions () { return compileOptions; }

    public TaskProvider<CxxCompileTask> getCompileTask ()
    {
        return compileTask;
    }

    @Override
    public String getName ()
    {
        return name;
    }

    public SourceDirectorySet getSources ()
    {
        return sources;
    }
}
