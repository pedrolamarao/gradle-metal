package br.dev.pedrolamarao.gradle.metal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class MetalTestBase
{
    @TempDir
    protected Path projectDir;

    @BeforeEach
    void gradleProperties () throws IOException
    {
        final var metalPath = System.getProperty("metal.path");
        if (metalPath != null) {
            Files.writeString(projectDir.resolve("gradle.properties"),"metal.path = %s".formatted(metalPath));
        }
    }
}
