// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal.base;

import org.gradle.api.NonNullApi;
import org.gradle.api.plugins.ExtensionAware;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.services.ServiceReference;

import java.io.File;

/**
 * Metal extension.
 */
@NonNullApi
public abstract class MetalExtension implements ExtensionAware
{
    // properties

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
        return getMetalService().map(MetalService::getHost);
    }

    /**
     * Project-wide link options.
     *
     * @return property
     */
    public abstract ListProperty<String> getLinkOptions ();

    // methods

    /**
     * Formats an archive file name according to the host conventions.
     *
     * @param name  core name
     * @return      file name
     */
    public Provider<String> archiveFileName (String name)
    {
        return getMetalService().map(it -> it.archiveFileName(name));
    }

    /**
     * Formats an executable file name according to the host conventions.
     *
     * @param name  core name
     * @return      file name
     */
    public Provider<String> executableFileName (String name)
    {
        return getMetalService().map(it -> it.executableFileName(name));
    }

    /**
     * Locate tool.
     *
     * @param name  tool name
     * @return      tool executable file provider
     */
    public Provider<File> locateTool (String name)
    {
        return getMetalService().flatMap(it -> it.locateTool(name));
    }
}
