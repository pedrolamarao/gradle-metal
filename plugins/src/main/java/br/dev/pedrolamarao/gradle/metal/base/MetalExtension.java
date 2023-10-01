// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal.base;

import org.gradle.api.NonNullApi;
import org.gradle.api.plugins.ExtensionAware;
import org.gradle.api.provider.ListProperty;

import javax.inject.Inject;

/**
 * Metal extension.
 */
@NonNullApi
public abstract class MetalExtension implements ExtensionAware
{
    /**
     * Project-wide archive options.
     *
     * @return property
     */
    public abstract ListProperty<String> getArchiveOptions ();

    /**
     * Project-wide compile options.
     *
     * @return property
     */
    public abstract ListProperty<String> getCompileOptions ();

    /**
     * Project-wide link options.
     *
     * @return property
     */
    public abstract ListProperty<String> getLinkOptions ();

    /**
     * Constructor.
     */
    @Inject
    public MetalExtension ()
    {
    }
}
