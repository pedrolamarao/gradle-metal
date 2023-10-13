package br.dev.pedrolamarao.gradle.metal.ixx;

import br.dev.pedrolamarao.gradle.metal.base.MetalSources;
import org.gradle.api.NonNullApi;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.Directory;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.TaskOutputs;
import org.gradle.api.tasks.TaskProvider;

import javax.inject.Inject;

/**
 * C++ module implementation sources.
 */
@NonNullApi
public abstract class MetalIxxSources extends MetalSources
{
    private final TaskProvider<MetalIxxCompileTask> compileTask;

    private final String name;

    /**
     * Constructor.
     *
     * @param compileTask compile task
     * @param name        source set name
     */
    @Inject
    public MetalIxxSources (TaskProvider<MetalIxxCompileTask> compileTask, String name)
    {
        this.compileTask = compileTask;
        this.name = name;

        getPublic().convention(false);
    }

    /**
     * Compile options.
     *
     * @return property
     */
    public abstract ListProperty<String> getCompileOptions ();

    /**
     * Imports.
     *
     * @return collection
     */
    public abstract ConfigurableFileCollection getImports ();

    /**
     * Includes.
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
     * If this source set is public and must be published.
     *
     * @return property
     */
    public abstract Property<Boolean> getPublic ();

    @Override
    public String toString ()
    {
        return "MetalIxxSources[%s]".formatted(name);
    }
}
