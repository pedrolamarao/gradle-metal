// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal.base;

import org.gradle.api.Named;
import org.gradle.api.NonNullApi;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.tasks.TaskProvider;

import javax.annotation.Nonnull;

/**
 * Metal application component.
 */
@NonNullApi
public class MetalApplication extends MetalComponent implements Named
{
    private final ListProperty<String> linkOptions;

    private final TaskProvider<MetalLinkTask> linkTask;

    private final String name;

    /**
     * Constructor.
     *
     * @param linkOptions  link options
     * @param linkTask     link task
     * @param name         name
     */
    public MetalApplication (ListProperty<String> linkOptions, TaskProvider<MetalLinkTask> linkTask, String name)
    {
        this.linkOptions = linkOptions;
        this.linkTask = linkTask;
        this.name = name;
    }

    /**
     * Link options.
     *
     * @return property
     */
    @Nonnull
    public ListProperty<String> getLinkOptions ()
    {
        return linkOptions;
    }

    /**
     * Link task.
     *
     * @return provider
     */
    @Nonnull
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

    /**
     * {@inheritDoc}
     */
    @Override
    public void source (Object... sources)
    {
        linkTask.configure(it -> it.source(sources));
    }
}
