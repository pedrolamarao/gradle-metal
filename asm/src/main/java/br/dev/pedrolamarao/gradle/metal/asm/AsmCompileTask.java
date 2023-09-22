// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal.asm;

import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.*;
import org.gradle.api.tasks.options.Option;
import org.gradle.workers.WorkerExecutor;

import javax.inject.Inject;

public abstract class AsmCompileTask extends SourceTask
{
    final WorkerExecutor workerExecutor;

    @InputFiles
    public abstract ConfigurableFileCollection getModules ();

    @Input
    public abstract ListProperty<String> getCompileOptions ();

    @OutputDirectory
    public abstract DirectoryProperty getOutputDirectory ();

    @Inject
    public AsmCompileTask (WorkerExecutor workerExecutor)
    {
        this.workerExecutor = workerExecutor;
    }

    @TaskAction
    public void compile ()
    {
        final var baseDirectory = getProject().getProjectDir();
        final var outputDirectory = getOutputDirectory();
        final var queue = workerExecutor.noIsolation();

        // remove old objects
        getProject().delete(outputDirectory);

        // assemble objects from sources
        getSource().forEach(source ->
        {
            queue.submit(AsmCompileWorkAction.class, parameters ->
            {
                parameters.getBaseDirectory().set(baseDirectory);
                parameters.getOutputDirectory().set(outputDirectory);
                parameters.getOptions().set(getCompileOptions());
                parameters.getSourceFile().set(source);
            });
        });
    }
}
