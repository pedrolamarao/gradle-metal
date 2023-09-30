package br.dev.pedrolamarao.gradle.metal.asm;

import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AsmFunctionalTest
{
    @TempDir Path projectDir;

    @Test
    public void compile () throws IOException
    {
        Files.createDirectories(projectDir.resolve("src/main/asm"));
        Files.createDirectories(projectDir.resolve("src/main/cpp"));

        Files.writeString(projectDir.resolve("src/main/cpp/foo.h"),
        """
        extern "C" void foo ();
        """
        );

        Files.writeString(projectDir.resolve("src/main/asm/bar.s"),
        """
        #include <foo.h>
        
        .global bar
        bar:
            call foo
            ret
        """
        );

        Files.writeString(projectDir.resolve("build.gradle.kts"),
        """
        plugins {
            id("br.dev.pedrolamarao.metal.asm")
        }
        
        metal {
            cpp {
                create("main")
            }
            asm {
                create("main") {
                    includable( cpp.named("main").map { it.sources.sourceDirectories } )
                }
            }
        }
        """
        );

        GradleRunner.create()
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .withArguments("compile-main-asm")
            .withDebug(true)
            .build();

        try (var stream = Files.walk(projectDir.resolve("build/obj")).filter(Files::isRegularFile)) {
            assertEquals( 1, stream.count() );
        }
    }
}
