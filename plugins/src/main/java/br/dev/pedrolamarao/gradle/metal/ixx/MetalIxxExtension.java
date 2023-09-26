package br.dev.pedrolamarao.gradle.metal.ixx;

import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.plugins.ExtensionAware;

public abstract class MetalIxxExtension implements ExtensionAware
{
    private final NamedDomainObjectContainer<MetalIxxSources> sources;

    public MetalIxxExtension (NamedDomainObjectContainer<MetalIxxSources> sources)
    {
        this.sources = sources;
    }

    public NamedDomainObjectContainer<MetalIxxSources> getSources ()
    {
        return sources;
    }
}
