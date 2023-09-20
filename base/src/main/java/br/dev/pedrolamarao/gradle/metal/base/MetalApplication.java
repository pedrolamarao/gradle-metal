package br.dev.pedrolamarao.gradle.metal.base;

import org.gradle.api.NonNullApi;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.tasks.TaskProvider;

import javax.annotation.Nonnull;

public class MetalApplication
{
    final ListProperty<String> linkOptions;

    final TaskProvider<NativeLinkTask> linKTask;

    public MetalApplication (ListProperty<String> linkOptions, TaskProvider<NativeLinkTask> linKTask)
    {
        this.linkOptions = linkOptions;
        this.linKTask = linKTask;
    }

    @Nonnull
    public ListProperty<String> getLinkOptions ()
    {
        return linkOptions;
    }

    @Nonnull
    public TaskProvider<NativeLinkTask> getLinkTask ()
    {
        return linKTask;
    }
}
