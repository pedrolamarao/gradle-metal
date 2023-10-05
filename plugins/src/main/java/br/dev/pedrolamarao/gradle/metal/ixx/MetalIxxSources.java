package br.dev.pedrolamarao.gradle.metal.ixx;

import org.gradle.api.Named;
import org.gradle.api.NonNullApi;
import org.gradle.api.file.Directory;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.TaskOutputs;
import org.gradle.api.tasks.TaskProvider;

import javax.inject.Inject;

/**
 * C++ module implementation sources.
 */
@NonNullApi
public class MetalIxxSources implements Named
{
    private final ListProperty<String> compileOptions;

    private final TaskProvider<MetalIxxCommandsTask> commandsTask;

    private final TaskProvider<MetalIxxCompileTask> compileTask;

    private final String name;

    /**
     * Constructor.
     *
     * @param compileOptions compile options
     * @param commandsTask
     * @param compileTask    compile task
     * @param name           name
     */
    @Inject
    public MetalIxxSources (ListProperty<String> compileOptions, TaskProvider<MetalIxxCommandsTask> commandsTask, TaskProvider<MetalIxxCompileTask> compileTask, String name)
    {
        this.compileOptions = compileOptions;
        this.commandsTask = commandsTask;
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
     * Compilation output base directory.
     *
     * @return provider
     */
    public Provider<Directory> getOutputDirectory ()
    {
        return compileTask.flatMap(MetalIxxCompileTask::getTargetDirectory);
    }

    /**
     * Compilation outputs.
     *
     * @return provider
     */
    public Provider<TaskOutputs> getOutputs ()
    {
        return compileTask.map(MetalIxxCompileTask::getOutputs);
    }

    /**
     * Adds directories to the include path.
     * .
     * @param sources  sources to add
     */
    public void includable (Object... sources)
    {
        commandsTask.configure(it -> it.getIncludables().from(sources));
        compileTask.configure(it -> it.getIncludables().from(sources));
    }

    /**
     * Adds directories to the import path.
     *
     * @param sources  sources to add
     */
    public void importable (Object... sources)
    {
        commandsTask.configure(it -> it.getImportables().from(sources));
        compileTask.configure(it -> it.getImportables().from(sources));
    }
}
