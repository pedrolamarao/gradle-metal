// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal;

import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.services.ServiceReference;
import org.gradle.api.tasks.*;
import org.gradle.process.ExecOperations;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

/**
 * Gradle Metal archiver task.
 */
@CacheableTask
public abstract class MetalArchive extends SourceTask
{
    /**
     * Archiver tool.
     *
     * @return property
     */
    @Input
    public abstract Property<String> getArchiver ();

    /**
     * Archiver options.
     *
     * @return property
     */
    @Input
    public abstract ListProperty<String> getOptions ();

    /**
     * Archiver output file.
     *
     * @return property
     */
    @OutputFile
    public abstract RegularFileProperty getOutput ();

    /**
     * Archiver target.
     *
     * @return property
     */
    @Input
    public abstract Property<String> getTarget ();

    /**
     * ExecOperations service.
     *
     * @return service
     */
    @Inject
    protected abstract ExecOperations getExec ();

    /**
     * Gradle Metal service.
     *
     * @return service
     */
    @ServiceReference
    protected abstract Property<MetalService> getMetal ();

    /**
     * Constructor.
     */
    @Inject
    public MetalArchive ()
    {
        getArchiver().convention("llvm-ar");
        getTarget().convention(getMetal().map(MetalService::getTarget));
    }

    /**
     * Archive action.
     */
    @TaskAction
    public void archive ()
    {
        final var archiver = getMetal().get().locateTool(getArchiver().get());
        final var options = getOptions().get();
        final var output = getOutput().getAsFile().get();

        try
        {
            Files.createDirectories(output.toPath().getParent());
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        final var args = new ArrayList<String>();
        args.add("rcs");
        args.addAll(options);
        args.add(output.toString());
        getSource().forEach(source -> args.add(source.toString()));

        getExec().exec(it -> {
            it.executable(archiver);
            it.args(args);
        });
    }
}
