// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal;

import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.SetProperty;

/**
 * Gradle Metal library extension.
 */
public abstract class MetalLibrary
{
    /**
     * Compiler options.
     *
     * @return property
     */
    public abstract ListProperty<String> getCompileOptions ();

    /**
     * Allowed targets.
     *
     * @return property
     */
    public abstract SetProperty<String> getTargets ();
}
