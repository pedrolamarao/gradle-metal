// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal.asm;

import org.gradle.api.file.FileCollection;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.tasks.TaskProvider;

import javax.inject.Inject;

public class AsmSources
{
    final TaskProvider<AsmCompileTask> compileTask;

    final FileCollection objects;

    final SourceDirectorySet sources;

    @Inject
    public AsmSources (TaskProvider<AsmCompileTask> compileTask, FileCollection objects, SourceDirectorySet sources)
    {
        this.compileTask = compileTask;
        this.objects = objects;
        this.sources = sources;
    }

    public TaskProvider<AsmCompileTask> getCompileTask ()
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
