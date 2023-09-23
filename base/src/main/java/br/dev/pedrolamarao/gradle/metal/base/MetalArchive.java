// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal.base;

import org.gradle.api.NonNullApi;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.tasks.TaskProvider;

import javax.annotation.Nonnull;

public class MetalArchive
{
    final ListProperty<String> archiveOptions;

    final TaskProvider<NativeArchiveTask> archiveTask;

    public MetalArchive (ListProperty<String> archiveOptions, TaskProvider<NativeArchiveTask> archiveTask)
    {
        this.archiveOptions = archiveOptions;
        this.archiveTask = archiveTask;
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
}
