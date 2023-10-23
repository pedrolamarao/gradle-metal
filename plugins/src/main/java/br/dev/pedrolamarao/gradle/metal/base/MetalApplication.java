// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal.base;

import org.gradle.api.Named;
import org.gradle.api.NonNullApi;
import org.gradle.api.provider.ListProperty;

import javax.inject.Inject;

/**
 * Metal application component.
 */
@NonNullApi
public abstract class MetalApplication extends MetalComponent implements Named
{
    private final String name;

    /**
     * Constructor.
     *
     * @param name  component name
     */
    @Inject
    public MetalApplication (String name)
    {
        this.name = name;
    }

    /**
     * Link options.
     *
     * @return property
     */
    public abstract ListProperty<String> getLinkOptions ();

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName ()
    {
        return name;
    }
}
