// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal.base;

import org.gradle.api.attributes.Attribute;

public enum MetalCapability
{
    COMMANDS,
    EXECUTABLE,
    IMPORTABLE,
    INCLUDABLE,
    LINKABLE,
    NONE;

    public static final Attribute<MetalCapability> ATTRIBUTE = Attribute.of(MetalCapability.class);
}
