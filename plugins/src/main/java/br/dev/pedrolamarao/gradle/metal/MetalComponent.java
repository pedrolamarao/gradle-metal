package br.dev.pedrolamarao.gradle.metal;

import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.SetProperty;

public abstract class MetalComponent
{
    /**
     * Compile options.
     *
     * @return property
     */
    public abstract ListProperty<String> getCompileOptions ();

    /**
     * Allowed targets.
     *
     * @return property
     */
    public abstract SetProperty<String> getTargets ();

    abstract ConfigurableFileCollection getObjectFiles ();

    abstract ConfigurableFileCollection getTestObjectFiles ();
}
