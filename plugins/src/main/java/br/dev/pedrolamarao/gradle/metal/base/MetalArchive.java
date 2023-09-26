// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal.base;

import org.gradle.api.Named;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.tasks.TaskProvider;

import javax.annotation.Nonnull;

public class MetalArchive extends MetalComponent implements Named
{
    private final ListProperty<String> archiveOptions;

    private final TaskProvider<NativeArchiveTask> archiveTask;

    private final String name;

    public MetalArchive (ListProperty<String> archiveOptions, TaskProvider<NativeArchiveTask> archiveTask, String name)
    {
        this.archiveOptions = archiveOptions;
        this.archiveTask = archiveTask;
        this.name = name;
    }

    @Nonnull
    public ListProperty<String> getArchiveOptions ()
    {
        return archiveOptions;
    }

    @Nonnull
    public TaskProvider<NativeArchiveTask> getArchiveTask ()
    {
        return archiveTask;
    }

    @Override
    public String getName ()
    {
        return name;
    }

    public void source (Object... sources)
    {
        archiveTask.configure(it -> it.source(sources));
    }
}
