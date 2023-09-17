package br.dev.pedrolamarao.gradle.cxx.language;

import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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
            source = file("main.cpp")
            target = file("main.o")
        }
        """;

        Files.writeString(projectDir.resolve("build.gradle.kts"), buildGradleKts);

        GradleRunner.create()
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .withArguments("compile")
            .build();

        assertTrue( Files.exists( projectDir.resolve("main.o") ) );
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
                source = file("main.cpp")
                target = file("main.o")
            }
            """;

        Files.writeString(projectDir.resolve("build.gradle.kts"), buildGradleKts);

        GradleRunner.create()
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .withArguments("compile")
            .build();

        assertTrue( Files.exists( projectDir.resolve("main.o") ) );
    }

    @Test
    public void link () throws Exception
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
            
            val compile = tasks.register<br.dev.pedrolamarao.gradle.cxx.language.CxxCompileTask>("compile") {
                source = file("main.cpp")
                target = file("main.o")
            }
            
            val link = tasks.register<br.dev.pedrolamarao.gradle.cxx.language.CxxLinkTask>("link") {
                source = compile.get().source
                target = file("executable")
            }
            """;

        Files.writeString(projectDir.resolve("build.gradle.kts"), buildGradleKts);

        GradleRunner.create()
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .withArguments("link")
            .build();

        assertTrue( Files.exists( projectDir.resolve("executable") ) );
    }

    @Test
    public void linkOptions () throws Exception
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
            
            val compile = tasks.register<br.dev.pedrolamarao.gradle.cxx.language.CxxCompileTask>("compile") {
                source = file("main.cpp")
                target = file("main.o")
            }
            
            val link = tasks.register<br.dev.pedrolamarao.gradle.cxx.language.CxxLinkTask>("link") {
                options = listOf("-g")
                source = compile.get().source
                target = file("executable")
            }
            """;

        Files.writeString(projectDir.resolve("build.gradle.kts"), buildGradleKts);

        GradleRunner.create()
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .withArguments("link")
            .build();

        assertTrue( Files.exists( projectDir.resolve("executable") ) );
    }
}
