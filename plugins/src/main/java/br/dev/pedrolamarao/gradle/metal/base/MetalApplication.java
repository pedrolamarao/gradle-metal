// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal.base;

import org.gradle.api.Named;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.tasks.TaskProvider;

import javax.annotation.Nonnull;

public class MetalApplication implements Named
{
    private final ListProperty<String> linkOptions;

    private final TaskProvider<NativeLinkTask> linkTask;

    private final String name;

    public MetalApplication (ListProperty<String> linkOptions, TaskProvider<NativeLinkTask> linkTask, String name)
    {
        this.linkOptions = linkOptions;
        this.linkTask = linkTask;
        this.name = name;
    }

    @Nonnull
    public ListProperty<String> getLinkOptions ()
    {
        return linkOptions;
    }

    @Nonnull
    public TaskProvider<NativeLinkTask> getLinkTask ()
    {
        return linkTask;
    }

    public String getName ()
    {
        return name;
    }

    public void source (Object... sources)
    {
        linkTask.configure(it -> it.source(sources));
    }
}
