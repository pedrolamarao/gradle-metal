// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal;

import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.SetProperty;

/**
 * Gradle Metal application extension.
 */
public abstract class MetalApplication
{
    /**
     * Compile options.
     *
     * @return property
     */
    public abstract ListProperty<String> getCompileOptions ();

    /**
     * Link options.
     *
     * @return property
     */
    public abstract ListProperty<String> getLinkOptions ();

    /**
     * Allowed targets.
     *
     * @return property
     */
    public abstract SetProperty<String> getTargets ();
}
