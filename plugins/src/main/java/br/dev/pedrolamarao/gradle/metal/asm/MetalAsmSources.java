package br.dev.pedrolamarao.gradle.metal.asm;

import br.dev.pedrolamarao.gradle.metal.base.MetalSources;
import org.gradle.api.DefaultTask;
import org.gradle.api.NonNullApi;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.TaskOutputs;
import org.gradle.api.tasks.TaskProvider;

import javax.inject.Inject;

/**
 * Assembler sources.
 */
@NonNullApi
public abstract class MetalAsmSources extends MetalSources
{
    private final TaskProvider<MetalAsmCommandsTask> commandsTask;

    private final TaskProvider<MetalAsmCompileTask> compileTask;

    private final String name;

    /**
     * Constructor.
     *
     * @param commandsTask    commands task
     * @param compileTask     compile task
     * @param name            name
     */
    @Inject
    public MetalAsmSources (TaskProvider<MetalAsmCommandsTask> commandsTask, TaskProvider<MetalAsmCompileTask> compileTask, String name)
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

    @Override
    public String toString ()
    {
        return "MetalAsmSources[%s]".formatted(name);
    }
}
