// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

public abstract class MetalTestBase
{
    @TempDir
    protected Path projectDir;

    protected String metalPathProperty = "";

    @BeforeEach
    void gradleProperties ()
    {
        final var metalPath = System.getProperty("metal.path");
        if (metalPath != null) {
            metalPathProperty = "-Pmetal.path=%s".formatted(metalPath);
        }
    }
}
