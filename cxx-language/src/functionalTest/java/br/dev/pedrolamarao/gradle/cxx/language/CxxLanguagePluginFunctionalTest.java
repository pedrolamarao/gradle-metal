package br.dev.pedrolamarao.gradle.cxx.language;

import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CxxLanguagePluginFunctionalTest
{
    @TempDir Path projectDir;

    @Test
    public void apply () throws IOException
    {
        Files.createDirectories(projectDir);

        final var buildGradleKts =
            """
            plugins {
                id("br.dev.pedrolamarao.cxx.language")
            }
            """;

        Files.writeString(projectDir.resolve("build.gradle.kts"), buildGradleKts);

        GradleRunner.create()
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();
    }

    @Test
    public void compile () throws Exception
    {
        Files.createDirectories(projectDir);

        final var mainCpp =
        """
        int main (int argc, char * argv[])
        {
            return 0;
        }
        """;

        Files.writeString(projectDir.resolve("main.cpp"), mainCpp);

        final var buildGradleKts =
        """
        plugins {
            id("br.dev.pedrolamarao.cxx.language")
        }
        
        tasks.register<br.dev.pedrolamarao.gradle.cxx.language.CxxCompileTask>("compile") {
            outputDirectory = file("object")
            source(file("main.cpp"))
        }
        """;

        Files.writeString(projectDir.resolve("build.gradle.kts"), buildGradleKts);

        GradleRunner.create()
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .withArguments("compile")
            .build();

        try (var stream = Files.list(projectDir.resolve("object"))) {
            assertEquals(1, stream.count() );
        }
    }

    @Test
    public void compileOptions () throws Exception
    {
        Files.createDirectories(projectDir);

        final var mainCpp =
            """
            int main (int argc, char * argv[])
            {
                return 0;
            }
            """;

        Files.writeString(projectDir.resolve("main.cpp"), mainCpp);

        final var buildGradleKts =
            """
            plugins {
                id("br.dev.pedrolamarao.cxx.language")
            }
            
            tasks.register<br.dev.pedrolamarao.gradle.cxx.language.CxxCompileTask>("compile") {
                options = listOf("-g")
                outputDirectory = file("object")
                source(file("main.cpp"))
            }
            """;

        Files.writeString(projectDir.resolve("build.gradle.kts"), buildGradleKts);

        GradleRunner.create()
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .withArguments("compile")
            .build();

        try (var stream = Files.list(projectDir.resolve("object"))) {
            assertEquals(1, stream.count() );
        }
    }
}
