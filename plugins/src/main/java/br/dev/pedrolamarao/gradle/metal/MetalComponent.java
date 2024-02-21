package br.dev.pedrolamarao.gradle.metal;

import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Provider;
import org.gradle.api.provider.SetProperty;
import org.gradle.api.tasks.util.PatternFilterable;

/**
 * Gradle Metal component extension.
 */
public interface MetalComponent extends PatternFilterable
{
    /**
     * Compile options.
     *
     * @return property
     */
    ListProperty<String> getCompileOptions ();

    /**
     * Build target.
     *
     * @return provider
     */
    Provider<String> getTarget ();

    /**
     * Allowed build targets.
     *
     * <p>This component builds only for allowed targets. Default is any target.</p>
     *
     * @return property
     */
    SetProperty<String> getTargets ();
}
