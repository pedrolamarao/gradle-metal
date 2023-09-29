// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal.base;

import org.gradle.api.attributes.Attribute;

public enum MetalVisibility
{
    COMPILE,
    RUN;

    public static final Attribute<MetalVisibility> ATTRIBUTE = Attribute.of(MetalVisibility.class);
}
