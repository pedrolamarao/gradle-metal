// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal.base;

import org.gradle.api.attributes.AttributeCompatibilityRule;
import org.gradle.api.attributes.CompatibilityCheckDetails;

/**
 * Compatibility rule for MetalCapability attributes.
 */
public class MetalCapabilityCompatibilityRule implements AttributeCompatibilityRule<MetalCapability>
{
    @Override
    public void execute (CompatibilityCheckDetails<MetalCapability> it)
    {
        if (it.getProducerValue() == MetalCapability.NONE) {
            it.compatible();
        }
    }
}
