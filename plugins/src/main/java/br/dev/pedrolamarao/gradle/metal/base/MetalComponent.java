package br.dev.pedrolamarao.gradle.metal.base;

/**
 * Metal component.
 */
public abstract class MetalComponent
{
    /**
     * Adds sources to component assembly.
     *
     * @param sources  sources to add
     */
    public abstract void source (Object... sources);
}
