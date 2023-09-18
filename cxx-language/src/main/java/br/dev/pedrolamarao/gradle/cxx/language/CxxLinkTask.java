package br.dev.pedrolamarao.gradle.cxx.language;

import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.*;
import org.gradle.api.tasks.options.Option;
import org.gradle.process.ExecOperations;
import org.gradle.workers.WorkerExecutor;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

public abstract class CxxLinkTask extends SourceTask
{
    final ExecOperations execOperations;

    @Input
    public abstract ListProperty<String> getOptions ();

    @OutputFile
    public abstract RegularFileProperty getOutput ();

    @Inject
    public CxxLinkTask (ExecOperations execOperations)
    {
        this.execOperations = execOperations;
    }

    @TaskAction
    public void compile ()
    {
        final var target = getOutput().getAsFile().get().toPath();

        final var command = new ArrayList<String>();
        command.add("clang");
        command.addAll(getOptions().get());
        command.add("-o");
        command.add(target.toString());
        getSource().forEach(file -> command.add(file.toString()));

        execOperations.exec(it ->
        {
            it.commandLine(command);
        });
    }
}
