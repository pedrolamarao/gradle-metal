package br.dev.pedrolamarao.gradle.cxx.language;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;

import java.io.IOException;
import java.util.ArrayList;

public abstract class CxxLinkTask extends DefaultTask
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
        command.add("-o");
        command.add(getTarget().getAsFile().get().getPath());
        command.add(getSource().getAsFile().get().getPath());
        getLogger().info("{}", command);

        final var processBuilder = new ProcessBuilder();
        processBuilder.command(command);
        final var process = processBuilder.start();
        final var status = process.waitFor();
        if (status != 0) {
            getLogger().error("lld failed: {}",status);
        }
    }
}
