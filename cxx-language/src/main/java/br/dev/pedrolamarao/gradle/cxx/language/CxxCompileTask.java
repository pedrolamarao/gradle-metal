package br.dev.pedrolamarao.gradle.cxx.language;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.FileCollection;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.tasks.*;

import java.io.IOException;
import java.util.ArrayList;

public abstract class CxxCompileTask extends DefaultTask
{
    @Input
    public abstract ListProperty<String> getOptions ();

    @InputFile
    public abstract RegularFileProperty getSource ();

    @OutputFile
    public abstract RegularFileProperty getTarget ();

    @TaskAction
    public void compile () throws IOException, InterruptedException
    {
        final var command = new ArrayList<String>();
        command.add("clang");
        command.addAll(getOptions().get());
        command.add("-c");
        command.add(getSource().getAsFile().get().getPath());
        command.add("-o");
        command.add(getTarget().getAsFile().get().getPath());
        getLogger().info("{}", command);

        final var processBuilder = new ProcessBuilder();
        processBuilder.command(command);
        final var process = processBuilder.start();
        final var status = process.waitFor();
        if (status != 0) {
            getLogger().error("clang failed: {}",status);
        }
    }
}
