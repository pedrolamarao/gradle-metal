// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal.base;

import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFile;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.OutputFile;
import org.gradle.api.tasks.TaskAction;
import org.gradle.process.ExecOperations;

import javax.inject.Inject;
import java.util.ArrayList;

public abstract class MetalArchiveTask extends MetalSourceTask
{
    @Input
    public abstract ListProperty<String> getArchiveOptions ();

    @OutputFile
    public Provider<RegularFile> getOutput ()
    {
        final var target = getTarget().orElse("default").get();
        final var name = getProject().getName();
        return getOutputDirectory().map(it -> it.file("%s/%s.lib".formatted(target,name)));
    }

    @Internal
    public abstract DirectoryProperty getOutputDirectory ();

    @Inject
    protected abstract ExecOperations getExec ();

    @TaskAction
    public void archive ()
    {
        final var output = getOutput().get().getAsFile().toPath();

        final var command = new ArrayList<String>();
        command.add("llvm-ar");
        command.add("rcs");
        command.addAll(getArchiveOptions().get());
        command.add(output.toString());
        getSource().forEach(file -> command.add(file.toString()));

        getExec().exec(it ->
        {
            it.commandLine(command);
        });
    }
}
