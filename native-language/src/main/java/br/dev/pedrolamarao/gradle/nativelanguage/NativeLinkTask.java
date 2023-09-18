// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.nativelanguage;

import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.tasks.*;
import org.gradle.process.ExecOperations;

import javax.inject.Inject;
import java.util.ArrayList;

public abstract class NativeLinkTask extends SourceTask
{
    final ExecOperations execOperations;

    @Input
    public abstract ListProperty<String> getOptions ();

    @OutputFile
    public abstract RegularFileProperty getOutput ();

    @Inject
    public NativeLinkTask (ExecOperations execOperations)
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
