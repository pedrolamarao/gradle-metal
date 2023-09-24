// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal.cxx;

import org.gradle.api.Named;
import org.gradle.api.NonNullApi;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.tasks.TaskProvider;

@NonNullApi
public class IxxSources implements Named
{
    final ListProperty<String> compileOptions;

    final TaskProvider<IxxCompileTask> compileTask;

    final ConfigurableFileCollection importDependencies;

    final ConfigurableFileCollection includeDependencies;

    final String name;

    public IxxSources (ListProperty<String> compileOptions, TaskProvider<IxxCompileTask> compileTask, ConfigurableFileCollection importDependencies, ConfigurableFileCollection includeDependencies, String name)
    {
        this.compileOptions = compileOptions;
        this.compileTask = compileTask;
        this.importDependencies = importDependencies;
        this.includeDependencies = includeDependencies;
        this.name = name;
    }

    public ListProperty<String> getCompileOptions () { return compileOptions; }

    public TaskProvider<IxxCompileTask> getCompileTask ()
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
}
