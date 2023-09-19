package br.dev.pedrolamarao.gradle.nativelanguage;

import org.gradle.api.attributes.Attribute;

public enum NativeCapability
{
    EXECUTABLE,
    IMPORTABLE,
    INCLUDABLE,
    LINKABLE,
    NONE;

    public static final Attribute<NativeCapability> ATTRIBUTE = Attribute.of(NativeCapability.class);
}
