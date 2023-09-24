// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal.cxx;

import org.gradle.api.Named;
import org.gradle.api.NonNullApi;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.tasks.TaskProvider;

@NonNullApi
public class CxxSources implements Named
{
    final ListProperty<String> compileOptions;

    final TaskProvider<CxxCompileTask> compileTask;

    final ConfigurableFileCollection importDependencies;

    final ConfigurableFileCollection includeDependencies;

    final String name;

    final SourceDirectorySet sources;

    public CxxSources (ListProperty<String> compileOptions, TaskProvider<CxxCompileTask> compileTask, ConfigurableFileCollection importDependencies, ConfigurableFileCollection includeDependencies, String name, SourceDirectorySet sources)
    {
        this.compileOptions = compileOptions;
        this.compileTask = compileTask;
        this.importDependencies = importDependencies;
        this.includeDependencies = includeDependencies;
        this.name = name;
        this.sources = sources;
    }

    public ListProperty<String> getCompileOptions () { return compileOptions; }

    public TaskProvider<CxxCompileTask> getCompileTask ()
    {
        return compileTask;
    }

    public ConfigurableFileCollection getImportDependencies ()
    {
        return importDependencies;
    }

    public ConfigurableFileCollection getIncludeDependencies ()
    {
        return includeDependencies;
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
