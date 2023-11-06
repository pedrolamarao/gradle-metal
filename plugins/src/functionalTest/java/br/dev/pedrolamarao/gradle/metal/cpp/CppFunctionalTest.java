package br.dev.pedrolamarao.gradle.metal.cpp;

import br.dev.pedrolamarao.gradle.metal.MetalTestBase;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;

public class CppFunctionalTest extends MetalTestBase
{
    @Test
    public void apply () throws IOException
    {
        Files.createDirectories(projectDir);

        Files.writeString(projectDir.resolve("build.gradle.kts"),
        """
        plugins {
            id("br.dev.pedrolamarao.metal.cpp")
        }
        """
        );

        GradleRunner.create()
            .withArguments("--configuration-cache")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();
    }
}
