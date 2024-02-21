// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal;

import org.gradle.api.provider.ListProperty;

/**
 * Gradle Metal application extension.
 */
public interface MetalApplication extends MetalComponent
{
    /**
     * Link options.
     *
     * @return property
     */
    ListProperty<String> getLinkOptions ();
}
