package br.dev.pedrolamarao.gradle.metal.cpp;

import org.gradle.api.Named;
import org.gradle.api.file.SourceDirectorySet;

import javax.inject.Inject;

public class MetalCppSources implements Named
{
    private final String name;

    private final SourceDirectorySet sources;

    @Inject
    public MetalCppSources (String name, SourceDirectorySet sources)
    {
        this.name = name;
        this.sources = sources;
    }

    @Override
    public String getName ()
    {
        return name;
    }

    public SourceDirectorySet getSources ()
    {
        return sources;
    }
}
