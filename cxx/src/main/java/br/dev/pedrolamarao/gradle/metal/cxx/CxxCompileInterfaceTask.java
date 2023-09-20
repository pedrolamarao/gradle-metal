// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal.cxx;

import org.gradle.api.file.FileCollection;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.tasks.*;
import org.gradle.process.ExecOperations;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public abstract class CxxCompileInterfaceTask extends CxxCompileBaseTask
{
    final ExecOperations execOperations;

    final ObjectFactory objectFactory;

    @Internal
    public FileCollection getInterfaceFiles ()
    {
        final var collection = objectFactory.fileCollection();
        final var baseDirectory = getProject().getProjectDir().toPath();
        final var outputDirectory = getOutputDirectory().get().getAsFile().toPath();
        getSource().forEach(source -> collection.from(toOutputPath(baseDirectory,source.toPath(),outputDirectory)));
        return collection;
    }

    @Inject
    public CxxCompileInterfaceTask (ExecOperations execOperations, ObjectFactory objectFactory)
    {
        this.execOperations = execOperations;
        this.objectFactory = objectFactory;
    }

    @TaskAction
    public void compile () throws IOException
    {
        final var baseDirectory = getProject().getProjectDir().toPath();
        final var outputDirectory = getOutputDirectory().get().getAsFile().toPath();

        // #TODO: sort sources accordingly with dependencies
        if (getSource().getFiles().size() > 1) {
            getLogger().warn("Task currently compiles a source set with multiple module interface files out of order");
        }

        for (var source : getSource())
        {
            final var output = toOutputPath(baseDirectory,source.toPath(),outputDirectory);
            Files.createDirectories(output.getParent());

            final var command = new ArrayList<String>();
            command.add("clang++");
            getHeaderDependencies().forEach(file -> command.add("--include-directory=%s".formatted(file)));
            getModuleDependencies().forEach(file -> command.add("-fmodule-file=%s".formatted(file)));
            command.addAll(getCompileOptions().get());
            command.add("--precompile");
            command.add("--output=%s".formatted(output));
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
