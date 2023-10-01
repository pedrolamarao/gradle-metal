package br.dev.pedrolamarao.gradle.metal.asm;

import org.gradle.api.DefaultTask;
import org.gradle.api.Named;
import org.gradle.api.NonNullApi;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.TaskOutputs;
import org.gradle.api.tasks.TaskProvider;

import javax.inject.Inject;

/**
 * Assembler sources.
 */
@NonNullApi
public class MetalAsmSources implements Named
{
    private final TaskProvider<MetalAsmCommandsTask> commandsTask;

    private final ListProperty<String> compileOptions;

    private final TaskProvider<MetalAsmCompileTask> compileTask;

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
    public MetalAsmSources (TaskProvider<MetalAsmCommandsTask> commandsTask, ListProperty<String> compileOptions, TaskProvider<MetalAsmCompileTask> compileTask, String name)
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
        return compileTask.map(DefaultTask::getOutputs);
    }

    /**
     * Adds directories to include path.
     *
     * @param sources  sources to add
     */
    public void includable (Object... sources)
    {
        commandsTask.configure(it -> it.getIncludables().from(sources));
        compileTask.configure(it -> it.getIncludables().from(sources));
    }
}
