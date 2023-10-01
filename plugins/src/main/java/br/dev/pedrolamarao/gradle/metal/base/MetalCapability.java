// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal.base;

import org.gradle.api.attributes.Attribute;

/**
 * Metal artifacts capability.
 */
public enum MetalCapability
{
    /**
     * Commands artifacts.
     */
    COMMANDS,

    /**
     * Executable artifacts.
     */
    EXECUTABLE,

    /**
     * Importable artifacts.
     */
    IMPORTABLE,

    /**
     * Includable artifacts.
     */
    INCLUDABLE,

    /**
     * Linkable artifacts.
     */
    LINKABLE,

    /**
     * No capability artifacts.
     */
    NONE;

    /**
     * Capability attribute.
     */
    public static final Attribute<MetalCapability> ATTRIBUTE = Attribute.of(MetalCapability.class);
}
