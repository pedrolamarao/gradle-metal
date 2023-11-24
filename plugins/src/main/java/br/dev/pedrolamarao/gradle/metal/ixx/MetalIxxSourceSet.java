package br.dev.pedrolamarao.gradle.metal.ixx;

import br.dev.pedrolamarao.gradle.metal.base.MetalSourceSet;
import org.gradle.api.NonNullApi;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.FileCollection;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;

import javax.inject.Inject;

/**
 * C++ module implementation sources.
 */
@NonNullApi
public abstract class MetalIxxSourceSet extends MetalSourceSet
{
    private final FileCollection compilables;

    private final FileCollection importables;

    private final String name;

    /**
     * Constructor.
     *
     * @param compilables  compilable elements
     * @param importables  importable elements
     * @param name         source set name
     */
    @Inject
    public MetalIxxSourceSet (FileCollection compilables, FileCollection importables, String name)
    {
        this.compilables = compilables;
        this.importables = importables;
        this.name = name;

        getPublic().convention(false);
    }

    /**
     * Compile elements.
     *
     * @return collection
     */
    public FileCollection getCompilables ()
    {
        return compilables;
    }

    /**
     * Compile options.
     *
     * @return property
     */
    public abstract ListProperty<String> getCompileOptions ();

    /**
     * Import elements.
     *
     * @return collection
     */
    public FileCollection getImportables ()
    {
        return importables;
    }

    /**
     * Import dependencies.
     *
     * @return configurable collection
     */
    public abstract ConfigurableFileCollection getImport ();

    /**
     * Include dependencies.
     *
     * @return configurable collection
     */
    public abstract ConfigurableFileCollection getInclude ();

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName ()
    {
        return name;
    }

    /**
     * If this source set is public and publishes its import elements.
     *
     * @return property
     */
    public abstract Property<Boolean> getPublic ();

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString ()
    {
        return "MetalIxxSourceSet[%s]".formatted(name);
    }
}
