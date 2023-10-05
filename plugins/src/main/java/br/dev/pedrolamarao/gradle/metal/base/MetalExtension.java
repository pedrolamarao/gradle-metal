// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal.base;

import org.gradle.api.NonNullApi;
import org.gradle.api.plugins.ExtensionAware;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.services.ServiceReference;

/**
 * Metal extension.
 */
@NonNullApi
public abstract class MetalExtension implements ExtensionAware
{
    /**
     * Metal service.
     *
     * @return service
     */
    @ServiceReference
    protected abstract Property<MetalService> getMetalService ();

    /**
     * Project-wide archive options.
     *
     * @return property
     */
    public abstract ListProperty<String> getArchiveOptions ();

    /**
     * Project-wide compile options.
     *
     * @return property
     */
    public abstract ListProperty<String> getCompileOptions ();

    /**
     * Host target provider.
     *
     * @return provider
     */
    public Provider<String> getHostTarget ()
    {
        return getMetalService().flatMap(MetalService::getHostTarget);
    }

    /**
     * Project-wide link options.
     *
     * @return property
     */
    public abstract ListProperty<String> getLinkOptions ();
}
