// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.cxx.language;

import groovy.transform.Internal;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.*;
import org.gradle.api.tasks.options.Option;
import org.gradle.process.ExecOperations;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public abstract class CxxCompileInterfaceTask extends SourceTask
{
    final ExecOperations execOperations;

    @InputFile
    public abstract RegularFileProperty getDependencies ();

    @Input
    public abstract ListProperty<String> getOptions ();

    @OutputDirectory
    public abstract DirectoryProperty getOutputDirectory ();

    @Inject
    public CxxCompileInterfaceTask (ExecOperations execOperations)
    {
        this.execOperations = execOperations;
    }

    @TaskAction
    public void compile () throws IOException
    {
        final var baseDirectory = getProject().getProjectDir().toPath();
        final var outputDirectory = getOutputDirectory().get().getAsFile().toPath();

        // #TODO: sort sources accordingly with dependencies

        for (var source : getSource())
        {
            final var output = toOutputPath(baseDirectory,source.toPath(),outputDirectory);
            Files.createDirectories(output.getParent());

            final var command = new ArrayList<String>();
            command.add("clang++");
            command.addAll(getOptions().get());
            command.add("--precompile");
            command.add("-o");
            command.add(output.toString());
            command.add(source.toString());

            execOperations.exec(it -> {
                it.commandLine(command);
            });
        }
    }

    Path toOutputPath (Path baseDirectory, Path source, Path outputDirectory)
    {
        final var relative = baseDirectory.relativize(source);
        final var output = outputDirectory.resolve("%X".formatted(relative.hashCode()));
        return output.resolve(source.getFileName() + ".pcm");
    }
}
