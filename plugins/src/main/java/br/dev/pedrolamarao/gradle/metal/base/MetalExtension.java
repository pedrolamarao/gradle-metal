// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal.base;

import org.gradle.api.NonNullApi;
import org.gradle.api.plugins.ExtensionAware;
import org.gradle.api.provider.ListProperty;

import javax.inject.Inject;

@NonNullApi
public abstract class MetalExtension implements ExtensionAware
{
    public abstract ListProperty<String> getArchiveOptions ();

    public abstract ListProperty<String> getCompileOptions ();

    public abstract ListProperty<String> getLinkOptions ();

    @Inject
    public MetalExtension ()
    {
    }
}
