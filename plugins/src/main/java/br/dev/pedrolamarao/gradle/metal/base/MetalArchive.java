// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal.base;

import org.gradle.api.Named;
import org.gradle.api.NonNullApi;
import org.gradle.api.provider.ListProperty;

import javax.inject.Inject;

/**
 * Metal archive component.
 */
@NonNullApi
public abstract class MetalArchive extends MetalComponent implements Named
{
    private final String name;

    /**
     * Constructor.
     *
     * @param name   component name
     */
    @Inject
    public MetalArchive (String name)
    {
        this.name = name;
    }

    /**
     * Archive options.
     *
     * @return property
     */
    public abstract ListProperty<String> getArchiveOptions ();

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName ()
    {
        return name;
    }
}
