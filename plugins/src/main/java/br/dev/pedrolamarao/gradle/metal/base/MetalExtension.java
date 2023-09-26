// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal.base;

import org.gradle.api.plugins.ExtensionAware;

import javax.inject.Inject;

public abstract class MetalExtension implements ExtensionAware
{
    @Inject
    public MetalExtension ()
    {
    }
}
