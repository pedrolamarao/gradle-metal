package br.dev.pedrolamarao.gradle.metal.cxx;

import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PluginFunctionalTest
{
    @TempDir Path projectDir;

    @Test
    public void apply () throws IOException
    {
        Files.createDirectories(projectDir);

        final var buildGradleKts =
        """
        plugins {
            id("br.dev.pedrolamarao.metal.cxx")
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
        Files.createDirectories(projectDir.resolve("src"));

        final var mainCpp =
        """
        int main (int argc, char * argv[])
        {
            return 0;
        }
        """;

        Files.writeString(projectDir.resolve("src/main.cpp"), mainCpp);

        final var buildGradleKts =
        """
        plugins {
            id("br.dev.pedrolamarao.metal.cxx")
        }
        
        tasks.register<br.dev.pedrolamarao.gradle.metal.cxx.CxxCompileTask>("compile") {
            outputDirectory = file("obj")
            source("src")
        }
        """;

        Files.writeString(projectDir.resolve("build.gradle.kts"), buildGradleKts);

        GradleRunner.create()
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .withArguments("compile")
            .build();

        try (var stream = Files.list(projectDir.resolve("obj"))) {
            assertEquals(1, stream.count() );
        }
    }

    @Test
    public void compileOptions () throws Exception
    {
        Files.createDirectories(projectDir.resolve("src"));

        final var mainCpp =
        """
        int main (int argc, char * argv[])
        {
            return 0;
        }
        """;

        Files.writeString(projectDir.resolve("src/main.cpp"), mainCpp);

        final var buildGradleKts =
            """
            plugins {
                id("br.dev.pedrolamarao.metal.cxx")
            }
            
            tasks.register<br.dev.pedrolamarao.gradle.metal.cxx.CxxCompileTask>("compile") {
                compileOptions = listOf("-g")
                outputDirectory = file("obj")
                source("src")
            }
            """;

        Files.writeString(projectDir.resolve("build.gradle.kts"), buildGradleKts);

        GradleRunner.create()
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .withArguments("compile")
            .build();

        try (var stream = Files.list(projectDir.resolve("obj"))) {
            assertEquals(1, stream.count() );
        }
    }

    @Test
    public void compileDependencies () throws Exception
    {
        Files.createDirectories(projectDir.resolve("src"));

        final var fooIxx =
            """
            export module foo;
            
            export namespace foo
            {
                int f () { return 0; }
            }
            """;

        Files.writeString(projectDir.resolve("src/foo.ixx"), fooIxx);

        final var barIxx =
            """
            export module bar;
            
            import foo;
            
            export namespace bar
            {
                int g () { return foo::f(); }
            }
            """;

        Files.writeString(projectDir.resolve("src/bar.ixx"), barIxx);

        final var buildGradleKts =
            """
            plugins {
                id("br.dev.pedrolamarao.metal.cxx")
            }
            
            tasks.register<br.dev.pedrolamarao.gradle.metal.cxx.IxxCompileTask>("compile") {
                compileOptions = listOf("-g","-std=c++20")
                outputDirectory = file("bmi")
                source("src")
            }
            """;

        Files.writeString(projectDir.resolve("build.gradle.kts"), buildGradleKts);

        GradleRunner.create()
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .withArguments("compile")
            .build();

        try (var stream = Files.list(projectDir.resolve("bmi"))) {
            assertEquals(2, stream.count() );
        }
    }
}
