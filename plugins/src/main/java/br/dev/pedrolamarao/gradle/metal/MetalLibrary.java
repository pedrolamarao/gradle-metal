package br.dev.pedrolamarao.gradle.metal;

import org.gradle.api.provider.ListProperty;

public abstract class MetalLibrary
{
    public abstract ListProperty<String> getCompileOptions ();
}
