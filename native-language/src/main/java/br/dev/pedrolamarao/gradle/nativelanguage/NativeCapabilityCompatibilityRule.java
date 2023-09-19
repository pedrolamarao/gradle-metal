package br.dev.pedrolamarao.gradle.nativelanguage;

import org.gradle.api.attributes.AttributeCompatibilityRule;
import org.gradle.api.attributes.CompatibilityCheckDetails;

public class NativeCapabilityCompatibilityRule implements AttributeCompatibilityRule<NativeCapability>
{
    @Override
    public void execute (CompatibilityCheckDetails<NativeCapability> it)
    {
        if (it.getProducerValue() == NativeCapability.NONE) {
            it.compatible();
        }
    }
}
