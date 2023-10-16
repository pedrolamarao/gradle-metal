// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal.base;

import org.gradle.api.Named;
import org.gradle.api.NonNullApi;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.tasks.TaskProvider;

import javax.inject.Inject;

/**
 * Metal application component.
 */
@NonNullApi
public abstract class MetalApplication extends MetalComponent implements Named
{
    private final TaskProvider<MetalLinkTask> linkTask;

    private final String name;

    /**
     * Constructor.
     *
     * @param linkTask     link task
     * @param name         name
     */
    @Inject
    public MetalApplication (TaskProvider<MetalLinkTask> linkTask, String name)
    {
        this.linkTask = linkTask;
        this.name = name;
    }

    public abstract ConfigurableFileCollection getArchives ();

    /**
     * Link options.
     *
     * @return property
     */
    public abstract ListProperty<String> getLinkOptions ();

    /**
     * Link task.
     *
     * @return provider
     */
    public TaskProvider<MetalLinkTask> getLinkTask ()
    {
        return linkTask;
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
