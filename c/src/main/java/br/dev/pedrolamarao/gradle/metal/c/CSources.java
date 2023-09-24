// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal.c;

import org.gradle.api.Named;
import org.gradle.api.NonNullApi;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.tasks.TaskProvider;

@NonNullApi
public class CSources implements Named
{
    final ListProperty<String> compileOptions;

    final TaskProvider<CCompileTask> compileTask;

    final ConfigurableFileCollection includeDependencies;

    final String name;

    final SourceDirectorySet sources;

    public CSources (ListProperty<String> compileOptions, TaskProvider<CCompileTask> compileTask, ConfigurableFileCollection includeDependencies, String name, SourceDirectorySet sources)
    {
        this.compileOptions = compileOptions;
        this.compileTask = compileTask;
        this.includeDependencies = includeDependencies;
        this.name = name;
        this.sources = sources;
    }

    public ListProperty<String> getCompileOptions () { return compileOptions; }

    public TaskProvider<CCompileTask> getCompileTask ()
    {
        return compileTask;
    }

    public ConfigurableFileCollection getIncludeDependencies ()
    {
        return includeDependencies;
    }

    public String getName ()
    {
        return name;
    }

    public SourceDirectorySet getSources ()
    {
        return sources;
    }
}
