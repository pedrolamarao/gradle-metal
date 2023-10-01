// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal.base;

import org.gradle.api.Named;
import org.gradle.api.NonNullApi;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.tasks.TaskProvider;

/**
 * Metal archive component.
 */
@NonNullApi
public class MetalArchive extends MetalComponent implements Named
{
    private final ListProperty<String> archiveOptions;

    private final TaskProvider<MetalArchiveTask> archiveTask;

    private final String name;

    /**
     * Constructor.
     *
     * @param archiveOptions  archive options
     * @param archiveTask     archive task
     * @param name            name
     */
    public MetalArchive (ListProperty<String> archiveOptions, TaskProvider<MetalArchiveTask> archiveTask, String name)
    {
        this.archiveOptions = archiveOptions;
        this.archiveTask = archiveTask;
        this.name = name;
    }

    /**
     * Archive options.
     *
     * @return property
     */
    public ListProperty<String> getArchiveOptions ()
    {
        return archiveOptions;
    }

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

    /**
     * {@inheritDoc}
     */
    @Override
    public void source (Object... sources)
    {
        archiveTask.configure(it -> it.source(sources));
    }
}
