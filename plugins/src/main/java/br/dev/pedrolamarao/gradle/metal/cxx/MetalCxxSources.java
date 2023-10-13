package br.dev.pedrolamarao.gradle.metal.cxx;

import br.dev.pedrolamarao.gradle.metal.base.MetalSources;
import org.gradle.api.NonNullApi;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.TaskOutputs;
import org.gradle.api.tasks.TaskProvider;

import javax.inject.Inject;

/**
 * C++ sources.
 */
@NonNullApi
public abstract class MetalCxxSources extends MetalSources
{
    private final TaskProvider<MetalCxxCommandsTask> commandsTask;

    private final TaskProvider<MetalCxxCompileTask> compileTask;

    private final String name;

    /**
     * Constructor.
     *
     * @param commandsTask    commands task
     * @param compileTask     compile task
     * @param name            name
     */
    @Inject
    public MetalCxxSources (TaskProvider<MetalCxxCommandsTask> commandsTask, TaskProvider<MetalCxxCompileTask> compileTask, String name)
    {
        this.commandsTask = commandsTask;
        this.compileTask = compileTask;
        this.name = name;
    }

    /**
     * Compile options.
     *
     * @return property
     */
    public abstract ListProperty<String> getCompileOptions ();

    /**
     * Preprocessor includes.
     *
     * @return collection
     */
    public abstract ConfigurableFileCollection getIncludes ();

    /**
     * C++ imports.
     *
     * @return collection
     */
    public abstract ConfigurableFileCollection getImports ();

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName ()
    {
        return name;
    }

    /**
     * Compilation outputs.
     *
     * @return provider
     */
    public Provider<TaskOutputs> getOutputs ()
    {
        return compileTask.map(MetalCxxCompileTask::getOutputs);
    }

    @Override
    public String toString ()
    {
        return "MetalCxxSources[%s]".formatted(name);
    }
}
