package br.dev.pedrolamarao.gradle.metal.asm;

import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.plugins.ExtensionAware;

public abstract class MetalAsmExtension implements ExtensionAware
{
    private final NamedDomainObjectContainer<MetalAsmSources> sources;

    public MetalAsmExtension (NamedDomainObjectContainer<MetalAsmSources> sources)
    {
        this.sources = sources;
    }

    public NamedDomainObjectContainer<MetalAsmSources> getSources ()
    {
        return sources;
    }
}
