// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal.cxx;

import org.gradle.api.Named;
import org.gradle.api.NonNullApi;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.tasks.TaskProvider;

@NonNullApi
public class IxxSources implements Named
{
    final ListProperty<String> compileOptions;

    final TaskProvider<IxxCompileTask> compileTask;

    final String name;

    public IxxSources (ListProperty<String> compileOptions, TaskProvider<IxxCompileTask> compileTask, String name)
    {
        this.compileOptions = compileOptions;
        this.compileTask = compileTask;
        this.name = name;
    }

    public ListProperty<String> getCompileOptions () { return compileOptions; }

    public TaskProvider<IxxCompileTask> getCompileTask ()
    {
        return compileTask;
    }

    @Override
    public String getName ()
    {
        return name;
    }
}
