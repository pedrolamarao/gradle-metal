// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.asm.language;

import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.*;
import org.gradle.api.tasks.options.Option;
import org.gradle.workers.WorkerExecutor;

import javax.inject.Inject;
import java.nio.file.Path;

public abstract class AsmCompileTask extends SourceTask
{
    final WorkerExecutor workerExecutor;

    @InputFiles
    public abstract ConfigurableFileCollection getModules ();

    @Input
    public abstract ListProperty<String> getOptions ();

    @OutputDirectory
    public abstract DirectoryProperty getOutputDirectory ();

    @Input @Option(option="target",description="code generation target") @Optional
    public abstract Property<String> getTarget ();

    @Inject
    public AsmCompileTask (WorkerExecutor workerExecutor)
    {
        this.workerExecutor = workerExecutor;
    }

    @TaskAction
    public void compile ()
    {
        final var baseDirectory = getProject().getProjectDir();
        final var queue = workerExecutor.noIsolation();

        getSource().forEach(source ->
        {
            queue.submit(AsmCompileWorkAction.class, parameters ->
            {
                parameters.getBaseDirectory().set(baseDirectory);
                parameters.getOutputDirectory().set(getOutputDirectory());
                parameters.getOptions().set(getOptions());
                parameters.getSourceFile().set(source);
                parameters.getTargetMachine().set(getTarget());
            });
        });
    }
}
