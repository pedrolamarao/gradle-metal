// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal.cxx;

import org.gradle.api.file.RegularFile;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.stream.Collectors;

public abstract class CxxCompileCommandsTask extends CxxCompileBaseTask
{
    @Input
    public abstract Property<File> getObjectDirectory ();

    @Internal
    public Provider<RegularFile> getOutput ()
    {
        return getOutputDirectory().file("compile_commands.json");
    }

    static final String template = """
      {
        "directory": "%s",
        "arguments": [ %s ],
        "file": "%s",
        "output": "%s"
      },
    """;

    @TaskAction
    public void generate () throws IOException
    {
        final var command = new ArrayList<String>();
        command.add("clang++");
        getHeaderDependencies().forEach(file -> command.add("--include-directory=%s".formatted(file).replace("\\","\\\\")));
        command.addAll(getCompileOptions().get());

        final var arguments = command.stream().collect(Collectors.joining("\",\"","\"","\""));
        final var directory = getProject().getProjectDir().toString().replace("\\","\\\\");
        final var baseDirectory = getProject().getProjectDir().toPath();

        try (var writer = Files.newBufferedWriter(getOutput().get().getAsFile().toPath(),StandardCharsets.UTF_8))
        {
            writer.write("[\n");
            getSource().forEach(source ->
            {
                try
                {
                    final var file = baseDirectory.relativize(source.toPath()).toString().replace("\\","\\\\");
                    final var output = baseDirectory.relativize(
                            getObjectDirectory().get().toPath().resolve(
                                "%X/%s".formatted(file.hashCode(),source.toPath().getFileName() + ".o")
                            )
                        )
                        .toString().replace("\\","\\\\");
                    writer.write( template.formatted(directory, arguments + ", \"" + file + '\"', file, output) );
                }
                catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
            });
            writer.write(']');
        }
    }
}
