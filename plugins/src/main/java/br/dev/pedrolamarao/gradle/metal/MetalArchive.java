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
    }

    /**
     * Archive action.
     */
    @TaskAction
    public void archive () throws Exception
    {
        final var archiver = getMetal().get().locateTool(getArchiver().get());
        final var options = getOptions().get();
        final var output = getOutput().getAsFile().get();

        final var atFile = this.getTemporaryDir().toPath().resolve("sources");
        try (var writer = Files.newBufferedWriter(atFile)) {
            getSource().forEach(file -> {
                try { writer.write(file.toString().replace("\\","\\\\") + "\n"); }
                    catch (IOException e) { throw new RuntimeException(e); }
            });
        }

        final var command = new ArrayList<String>();
        command.add(archiver.toString());
        command.add("rcs");
        command.addAll(options);
        command.add(output.toString());
        command.add("@"+atFile);

        getExec().exec(it -> it.commandLine(command));
    }
}
