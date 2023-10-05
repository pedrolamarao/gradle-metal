package br.dev.pedrolamarao.gradle.metal.base;

import org.gradle.api.Named;

/**
 * Metal component.
 */
public abstract class MetalComponent implements Named
{
    /**
     * Adds sources to component assembly.
     *
     * @param sources  sources to add
     */
    public abstract void source (Object... sources);
}
