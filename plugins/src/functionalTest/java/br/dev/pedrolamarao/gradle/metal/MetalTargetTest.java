package br.dev.pedrolamarao.gradle.metal;

import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;

/**
 * Gradle Metal target support.
 */
public class MetalTargetTest extends MetalTestBase
{
    @Test
    public void targets () throws IOException
    {
        Files.createDirectories(projectDir.resolve("src/main/cpp"));
        Files.writeString(projectDir.resolve("src/main/cpp/main.h"),
            """
            int main ();
            """
        );

        Files.createDirectories(projectDir.resolve("src/main/ixx"));
        Files.writeString(projectDir.resolve("src/main/ixx/main.ixx"),
            """
            module;
            
            #include <main.h>
            
            export module main;
            
            export int mmain () { return main(); }
            """
        );

        Files.createDirectories(projectDir.resolve("src/main/cxx"));
        Files.writeString(projectDir.resolve("src/main/cxx/main.cxx"),
            """
            #include <main.h>
            
            int main ()
            {
                return 0;
            }
            """
        );

        Files.createDirectories(projectDir.resolve("src/test/ixx"));
        Files.writeString(projectDir.resolve("src/test/ixx/test.ixx"),
            """            
            export module test;
            
            export int mtest () { return 0; }
            """
        );

        Files.createDirectories(projectDir.resolve("src/test/cxx"));
        Files.writeString(projectDir.resolve("src/test/cxx/test.cxx"),
            """
            #include <main.h>
            
            import main;
            import test;
            
            int main (int argc, char * argv[])
            {
                return main() + mmain() + mtest();
            }
            """
        );
        Files.writeString(projectDir.resolve("build.gradle.kts"),
            """         
            plugins {
                id("br.dev.pedrolamarao.metal.library")
                id("br.dev.pedrolamarao.metal.cxx")
            }
            
            library {
                compileOptions = listOf("-std=c++20")
            }
            """
        );

        final var target_host = GradleRunner.create()
            .withArguments("--build-cache","--configuration-cache","--info","compileCxx")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();

        final var target_i686_elf = GradleRunner.create()
            .withArguments("--build-cache","--configuration-cache","--info","-Pmetal.target=i686-elf","compileCxx")
            .withPluginClasspath()
            .withProjectDir(projectDir.toFile())
            .build();
    }
}
