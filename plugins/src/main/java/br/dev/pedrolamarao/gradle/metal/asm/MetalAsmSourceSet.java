package br.dev.pedrolamarao.gradle.metal.asm;

import br.dev.pedrolamarao.gradle.metal.base.MetalSourceSet;
import org.gradle.api.NonNullApi;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.FileCollection;
import org.gradle.api.provider.ListProperty;

import javax.inject.Inject;

/**
 * Assembler source set.
 */
@NonNullApi
public abstract class MetalAsmSourceSet extends MetalSourceSet
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
    public MetalAsmSourceSet (FileCollection linkables, String name)
    {
        this.linkables = linkables;
        this.name = name;
    }

    /**
     * Compile options.
     *
     * @return property
     */
    public abstract ListProperty<String> getCompileOptions ();

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
     * Link elements.
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
        return "MetalAsmSourceSet[%s]".formatted(name);
    }
}
