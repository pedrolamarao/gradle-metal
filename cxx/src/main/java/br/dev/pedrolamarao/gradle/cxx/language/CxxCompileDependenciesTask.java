// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.cxx.language;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFile;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.*;
import org.gradle.process.ExecOperations;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

public abstract class CxxCompileDependenciesTask extends DefaultTask
{
    final ExecOperations execOperations;

    @InputFile
    public abstract RegularFileProperty getCommands ();

    @OutputDirectory
    public abstract DirectoryProperty getOutputDirectory ();

    @Internal
    public Provider<RegularFile> getOutput ()
    {
        return getOutputDirectory().file("p1689.json");
    }

    @Inject
    public CxxCompileDependenciesTask (ExecOperations execOperations)
    {
        this.execOperations = execOperations;
    }

    @TaskAction
    public void check () throws IOException
    {
        final var command = new ArrayList<String>();
        command.add("clang-scan-deps");
        command.add("--compilation-database=%s".formatted(getCommands().get().getAsFile()));
        command.add("--format=p1689");

        final var outputStream = Files.newOutputStream(getOutput().get().getAsFile().toPath());

        execOperations.exec(it ->
        {
            it.commandLine(command);
            it.setStandardOutput(outputStream);
        });
    }
}
