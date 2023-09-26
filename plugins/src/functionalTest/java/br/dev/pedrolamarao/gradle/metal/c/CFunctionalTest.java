package br.dev.pedrolamarao.gradle.metal.c;

import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CFunctionalTest
{
    @TempDir Path projectDir;

    @Test
    public void compile () throws IOException
    {
        Files.createDirectories(projectDir.resolve("src/main/cpp"));
        Files.createDirectories(projectDir.resolve("src/main/c"));

        final var greetH =
            """
            inline
            int greet (int argc, char * argv [])
            {
                return 0;
            }
            """;

        Files.writeString(projectDir.resolve("src/main/cpp/greet.h"),greetH);

        Files.createDirectories(projectDir.resolve("src/main/c"));

        final var mainC =
            """
            #include <greet.h>
            
            int main (int argc, char * argv [])
            {
                return greet(argc,argv);
            }
            """;

        Files.writeString(projectDir.resolve("src/main/c/main.c"),mainC);

        final var buildGradleKts =
            """
            plugins {
                id("br.dev.pedrolamarao.metal.c")
            }
            
            metal {
                cpp {
                    create("main")
                }
                c {
                    create("main") {
                        header( cpp.named("main").map { it.sources.sourceDirectories } )
                    }
                }
            }
            """;

        Files.writeString(projectDir.resolve("build.gradle.kts"), buildGradleKts);

        final var result = GradleRunner.create()
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .withArguments("compile-main-c")
            .withDebug(true)
            .build();

        try (var stream = Files.walk(projectDir.resolve("build/obj")).filter(Files::isRegularFile)) {
            assertEquals( 1, stream.count() );
        }
    }
}
