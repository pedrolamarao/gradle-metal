package br.dev.pedrolamarao.gradle.metal.cpp;

import br.dev.pedrolamarao.gradle.metal.base.MetalSourceSet;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.FileCollection;
import org.gradle.api.provider.Property;

import javax.inject.Inject;

/**
 * C preprocessor sources.
 */
public abstract class MetalCppSourceSet extends MetalSourceSet
{
    private final String name;

    /**
     * Constructor.
     *
     * @param name  name
     */
    @Inject
    public MetalCppSourceSet (String name)
    {
        this.name = name;

        getPublic().convention(false);
    }

    /**
     * Include elements.
     *
     * @return collection
     */
    public FileCollection getIncludables ()
    {
        return getSources();
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
     * If these sources a public and publishes its include elements.
     *
     * @return true if public
     */
    public abstract Property<Boolean> getPublic ();

    /**
     * Sources.
     *
     * @return configurable collection
     */
    public abstract ConfigurableFileCollection getSources ();

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString ()
    {
        return "MetalCppSources[%s]".formatted(name);
    }
}
