// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal.asm;

import org.gradle.api.Named;
import org.gradle.api.NonNullApi;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.tasks.TaskProvider;

import javax.inject.Inject;

@NonNullApi
public class AsmSources implements Named
{
    final TaskProvider<AsmCompileTask> compileTask;

    final ConfigurableFileCollection includeDependencies;

    final String name;

    final SourceDirectorySet sources;

    @Inject
    public AsmSources (TaskProvider<AsmCompileTask> compileTask, ConfigurableFileCollection includeDependencies, String name, SourceDirectorySet sources)
    {
        this.compileTask = compileTask;
        this.includeDependencies = includeDependencies;
        this.name = name;
        this.sources = sources;
    }

    public TaskProvider<AsmCompileTask> getCompileTask ()
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
