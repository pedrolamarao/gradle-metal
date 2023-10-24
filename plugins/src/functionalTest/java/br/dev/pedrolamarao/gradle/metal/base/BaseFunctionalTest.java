package br.dev.pedrolamarao.gradle.metal.base;

import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class BaseFunctionalTest
{
    @TempDir Path projectDir;

    @Test
    public void apply () throws IOException
    {
        Files.writeString(projectDir.resolve("build.gradle.kts"),
        """
        plugins {
             id("br.dev.pedrolamarao.metal.base")
        }
         
        metal {
            archiveOptions = listOf()
            compileOptions = listOf()
            linkOptions = listOf()
        }
        """
        );

        GradleRunner.create()
            .withArguments("--configuration-cache")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();
    }

    @Test
    public void host () throws Exception
    {
        Files.writeString(projectDir.resolve("build.gradle.kts"),
            """
            plugins {
                id("br.dev.pedrolamarao.metal.base")
            }
            
            tasks.register("host") {
                val host = metal.host
                doLast {
                    print("${host.get()}")
                }
            }
            """
        );

        final var result = GradleRunner.create()
            .withArguments("--configuration-cache","--quiet","host")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();
        assertFalse( result.getOutput().isEmpty() );
    }

    @Test
    public void locateTool () throws Exception
    {
        Files.writeString(projectDir.resolve("build.gradle.kts"),
        """
        plugins {
             id("br.dev.pedrolamarao.metal.base")
        }
        
        tasks.register("locateTool") {
            val clang = metal.locateTool("clang")
            doLast {
                print("${clang.get()}")
            }
        }
        """
        );

        final var result = GradleRunner.create()
            .withArguments("--configuration-cache","--quiet","locateTool")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();
        assertFalse( result.getOutput().isEmpty() );
    }

    @Test
    public void target () throws Exception
    {
        Files.writeString(projectDir.resolve("build.gradle.kts"),
        """
        plugins {
            id("br.dev.pedrolamarao.metal.base")
        }
        
        tasks.register("target") {
            val target = metal.target
            doLast {
                print("${target.get()}")
            }
        }
        """
        );

        final var result = GradleRunner.create()
            .withArguments("--configuration-cache","--quiet","target","-Pmetal.target=foo")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();
        assertEquals( "foo", result.getOutput() );
    }
}