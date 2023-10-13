// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal.base;

import org.gradle.api.Named;
import org.gradle.api.NonNullApi;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.tasks.TaskProvider;

import javax.inject.Inject;

/**
 * Metal archive component.
 */
@NonNullApi
public abstract class MetalArchive extends MetalComponent implements Named
{
    private final TaskProvider<MetalArchiveTask> archiveTask;

    private final String name;

    /**
     * Constructor.
     *
     * @param archiveTask     archive task
     * @param name            name
     */
    @Inject
    public MetalArchive (TaskProvider<MetalArchiveTask> archiveTask, String name)
    {
        this.archiveTask = archiveTask;
        this.name = name;
    }

    /**
     * Archive options.
     *
     * @return property
     */
    public abstract ListProperty<String> getArchiveOptions ();

    /**
     * Archive task.
     *
     * @return provider
     */
    public TaskProvider<MetalArchiveTask> getArchiveTask ()
    {
        return archiveTask;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName ()
    {
        return name;
    }
}
