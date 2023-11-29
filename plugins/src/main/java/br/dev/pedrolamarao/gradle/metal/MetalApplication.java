// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal;

import org.gradle.api.provider.ListProperty;

public abstract class MetalApplication
{
    public abstract ListProperty<String> getCompileOptions ();

    public abstract ListProperty<String> getLinkOptions ();
}
