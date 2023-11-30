// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal;

import org.gradle.api.attributes.Attribute;

/**
 * Metal dependency visibility.
 */
public enum MetalVisibility
{
    /**
     * Compile time visibility.
     */
    COMPILE,

    /**
     * Run time visibility.
     */
    RUN;

    /**
     * Metal dependency visibility attribute.
     */
    public static final Attribute<MetalVisibility> ATTRIBUTE = Attribute.of(MetalVisibility.class);
}
