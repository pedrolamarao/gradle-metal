package br.dev.pedrolamarao.gradle.metal.cpp;

import br.dev.pedrolamarao.gradle.metal.base.MetalSources;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.provider.Property;

import javax.inject.Inject;

/**
 * C preprocessor sources.
 */
public abstract class MetalCppSources extends MetalSources
{
    private final String name;

    /**
     * Constructor.
     *
     * @param name  name
     */
    @Inject
    public MetalCppSources (String name)
    {
        this.name = name;

        getPublic().convention(false);
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
     * If these sources a public and must be published.
     *
     * @return true if public
     */
    public abstract Property<Boolean> getPublic ();

    /**
     * Source directory set.
     *
     * @return value
     */
    public abstract ConfigurableFileCollection getSources ();

    @Override
    public String toString ()
    {
        return "MetalCppSources[%s]".formatted(name);
    }
}
