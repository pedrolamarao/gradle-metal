// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal;

import org.gradle.api.attributes.Attribute;
import org.gradle.api.attributes.AttributeCompatibilityRule;
import org.gradle.api.attributes.CompatibilityCheckDetails;

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

    /**
     * Compatibility rule for MetalCapability attributes.
     */
    public static class CompatibilityRule implements AttributeCompatibilityRule<MetalCapability>
    {
        @Override
        public void execute (CompatibilityCheckDetails<MetalCapability> it)
        {
            if (it.getProducerValue() == NONE) {
                it.compatible();
            }
        }
    }
}
