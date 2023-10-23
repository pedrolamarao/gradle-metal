package br.dev.pedrolamarao.gradle.metal.cxx;

import br.dev.pedrolamarao.gradle.metal.base.MetalSourceSet;
import org.gradle.api.NonNullApi;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.FileCollection;
import org.gradle.api.provider.ListProperty;

import javax.inject.Inject;

/**
 * C++ sources.
 */
@NonNullApi
public abstract class MetalCxxSourceSet extends MetalSourceSet
{
    private final FileCollection linkables;

    private final String name;

    /**
     * Constructor.
     *
     * @param linkables  linkable elements
     * @param name       source set name
     */
    @Inject
    public MetalCxxSourceSet (FileCollection linkables, String name)
    {
        this.linkables = linkables;
        this.name = name;
    }

    /**
     * Compile dependencies.
     *
     * @return configurable collection
     */
    public abstract ConfigurableFileCollection getCompile ();

    /**
     * Compile options.
     *
     * @return property
     */
    public abstract ListProperty<String> getCompileOptions ();

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
     * Linkable elements.
     *
     * @return collection
     */
    public FileCollection getLinkables ()
    {
        return linkables;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString ()
    {
        return "MetalCxxSourceSet[%s]".formatted(name);
    }
}
