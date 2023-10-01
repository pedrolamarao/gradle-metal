package br.dev.pedrolamarao.gradle.metal.cxx;

import org.gradle.api.Named;
import org.gradle.api.NonNullApi;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.TaskOutputs;
import org.gradle.api.tasks.TaskProvider;

import javax.inject.Inject;

/**
 * C++ sources.
 */
@NonNullApi
public class MetalCxxSources implements Named
{
    private final TaskProvider<MetalCxxCommandsTask> commandsTask;

    private final ListProperty<String> compileOptions;

    private final TaskProvider<MetalCxxCompileTask> compileTask;

    private final String name;

    /**
     * Constructor.
     *
     * @param commandsTask    commands task
     * @param compileOptions  compile options
     * @param compileTask     compile task
     * @param name            name
     */
    @Inject
    public MetalCxxSources (TaskProvider<MetalCxxCommandsTask> commandsTask, ListProperty<String> compileOptions, TaskProvider<MetalCxxCompileTask> compileTask, String name)
    {
        this.commandsTask = commandsTask;
        this.compileOptions = compileOptions;
        this.compileTask = compileTask;
        this.name = name;
    }

    /**
     * Compile options.
     *
     * @return property
     */
    public ListProperty<String> getCompileOptions ()
    {
        return compileOptions;
    }

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

    /**
     * Adds directories to the import path.
     *
     * @param sources sources to add
     */
    public void importable (Object... sources)
    {
        commandsTask.configure(it -> it.getImportables().from(sources));
        compileTask.configure(it -> it.getImportables().from(sources));
    }

    /**
     * Adds directories to the include path.
     *
     * @param sources sources to add
     */
    public void includable (Object... sources)
    {
        commandsTask.configure(it -> it.getIncludables().from(sources));
        compileTask.configure(it -> it.getIncludables().from(sources));
    }

    /**
     * Adds sources to compilation.
     *
     * @param sources sources to add
     */
    public void source (Object... sources)
    {
        commandsTask.configure(it -> it.source(sources));
        compileTask.configure(it -> it.source(sources));
    }
}
