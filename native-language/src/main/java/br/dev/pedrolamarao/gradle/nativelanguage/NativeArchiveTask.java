// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.nativelanguage;

import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.SourceTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.process.ExecOperations;

import javax.inject.Inject;
import java.util.ArrayList;

public abstract class NativeArchiveTask extends SourceTask
{
    final ExecOperations execOperations;

    @OutputFile
    public abstract RegularFileProperty getOutput ();

    @Inject
    public NativeArchiveTask (ExecOperations execOperations)
    {
        this.execOperations = execOperations;
    }

    @TaskAction
    public void archive ()
    {
        final var target = getOutput().getAsFile().get().toPath();

        final var command = new ArrayList<String>();
        command.add("llvm-ar");
        command.add("rcs");
        command.add(target.toString());
        getSource().forEach(file -> command.add(file.toString()));

        execOperations.exec(it ->
        {
            it.commandLine(command);
        });
    }
}
