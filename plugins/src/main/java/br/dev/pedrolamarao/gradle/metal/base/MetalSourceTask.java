package br.dev.pedrolamarao.gradle.metal.base;

import org.gradle.api.provider.Property;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.SourceTask;

import javax.inject.Inject;

public abstract class MetalSourceTask extends SourceTask
{
    @Input
    @Optional
    public abstract Property<String> getTarget ();

    @Inject
    protected abstract ProviderFactory getProviders ();

    @Inject
    public MetalSourceTask ()
    {
        getTarget().convention(getProviders().gradleProperty("metal.target"));
    }
}
