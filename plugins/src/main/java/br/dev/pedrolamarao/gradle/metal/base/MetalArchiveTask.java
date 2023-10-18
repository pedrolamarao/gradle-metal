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
import java.io.File;
import java.util.ArrayList;

/**
 * Archive native objects.
 */
public abstract class MetalArchiveTask extends MetalSourceTask
{
    /**
     * Archiver executable path.
     *
     * @return provider
     */
    @Input
    public Provider<File> getArchiver ()
    {
        return getMetal().map(it -> it.locateTool("llvm-ar"));
    }

    /**
     * Archive options.
     *
     * @return property
     */
    @Input
    public abstract ListProperty<String> getArchiveOptions ();

    /**
     * Output file.
     *
     * @return provider
     */
    @OutputFile
    public Provider<RegularFile> getOutput ()
    {
        return getOutputDirectory().map(out -> {
            final var target = getTarget().get();
            final var file = getMetal().get().archiveFileName(target,getProject().getName());
            return out.file("%s/%s".formatted(target,file));
        });
    }

    /**
     * Output base directory.
     *
     * @return property
     */
    @Internal
    public abstract DirectoryProperty getOutputDirectory ();

    /**
     * Exec operations service.
     *
     * @return service
     */
    @Inject
    protected abstract ExecOperations getExec ();

    /**
     * Archive objects.
     */
    @TaskAction
    public void archive ()
    {
        final var output = getOutput().get().getAsFile().toPath();

        final var command = new ArrayList<String>();
        command.add("rcs");
        command.addAll(getArchiveOptions().get());
        command.add(output.toString());
        getSource().forEach(file -> command.add(file.toString()));

        getExec().exec(it ->
        {
            it.executable(getArchiver().get());
            it.args(command);
        });
    }
}
