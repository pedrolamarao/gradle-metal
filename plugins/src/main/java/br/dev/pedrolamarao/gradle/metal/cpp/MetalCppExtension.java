package br.dev.pedrolamarao.gradle.metal.cpp;

import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.plugins.ExtensionAware;

public abstract class MetalCppExtension implements ExtensionAware
{
    private final NamedDomainObjectContainer<MetalCppSources> sources;

    public MetalCppExtension (NamedDomainObjectContainer<MetalCppSources> sources)
    {
        this.sources = sources;
    }

    public NamedDomainObjectContainer<MetalCppSources> getSources ()
    {
        return sources;
    }
}
