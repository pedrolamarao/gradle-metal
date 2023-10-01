package br.dev.pedrolamarao.gradle.metal.cpp;

import org.gradle.api.Named;
import org.gradle.api.file.SourceDirectorySet;

import javax.inject.Inject;

/**
 * C preprocessor sources.
 */
public class MetalCppSources implements Named
{
    private final String name;

    private final SourceDirectorySet sources;

    /**
     * Constructor.
     *
     * @param name     name
     * @param sources  sources
     */
    @Inject
    public MetalCppSources (String name, SourceDirectorySet sources)
    {
        this.name = name;
        this.sources = sources;
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
     * Source directory set.
     *
     * @return value
     */
    public SourceDirectorySet getSources ()
    {
        return sources;
    }
}
