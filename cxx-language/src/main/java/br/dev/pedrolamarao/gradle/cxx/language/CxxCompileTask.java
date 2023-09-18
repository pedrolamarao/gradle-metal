package br.dev.pedrolamarao.gradle.cxx.language;

import org.gradle.api.Project;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.*;
import org.gradle.api.tasks.options.Option;
import org.gradle.workers.WorkerExecutor;

import javax.inject.Inject;
import java.nio.file.Path;

public abstract class CxxCompileTask extends SourceTask
{
    final WorkerExecutor workerExecutor;

    @Input
    public abstract ListProperty<String> getOptions ();

    @OutputDirectory
    public abstract DirectoryProperty getOutputDirectory ();

    @Input @Option(option="target",description="code generation target") @Optional
    public abstract Property<String> getTarget ();

    @Inject
    public CxxCompileTask (WorkerExecutor workerExecutor)
    {
        this.workerExecutor = workerExecutor;
    }

    @TaskAction
    public void compile ()
    {
        final var queue = workerExecutor.noIsolation();

        getSource().forEach(source ->
        {
            queue.submit(CxxCompileWorkAction.class, parameters ->
            {
                final var output = toOutputPath(getProject(),source.toPath());
                parameters.getOutput().set(output.toFile());
                parameters.getOptions().set(getOptions());
                parameters.getSource().set(source);
                parameters.getTarget().set(getTarget());
            });
        });
    }

    Path toOutputPath (Project project, Path source)
    {
        final var relative = project.getProjectDir().toPath().relativize(source);
        final var target = getOutputDirectory().get().getAsFile().toPath().resolve("%X".formatted(relative.hashCode()));
        return target.resolve(source.getFileName() + ".o");
    }
}
