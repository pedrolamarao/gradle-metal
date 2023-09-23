// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal.base;

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
