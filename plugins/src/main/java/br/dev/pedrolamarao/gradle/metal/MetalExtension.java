// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal;

import org.gradle.api.NonNullApi;
import org.gradle.api.plugins.ExtensionAware;
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
     * Host name.
     *
     * @return provider
     */
    public Provider<String> getHost ()
    {
        return getMetalService().flatMap(MetalService::getHost);
    }

    /**
     * Tools path.
     *
     * @return provider
     */
    public Provider<String> getPath () { return getMetalService().map(MetalService::getPath); }

    /**
     * Target name.
     *
     * @return provider
     */
    public Provider<String> getTarget ()
    {
        return getMetalService().map(MetalService::getTarget);
    }

    // methods

    /**
     * Formats an archive file name according to the host conventions.
     *
     * @param name  core name
     * @return      file name
     */
    public Provider<String> archiveFileName (String name)
    {
        return getTarget().map(target -> Metal.archiveFileName(target,name));
    }

    /**
     * Formats an executable file name according to the host conventions.
     *
     * @param name  core name
     * @return      file name
     */
    public Provider<String> executableFileName (String name)
    {
        return getTarget().map(target -> Metal.executableFileName(target,name));
    }

    /**
     * Locate tool.
     *
     * @param name  tool name
     * @return      tool executable file provider
     */
    public Provider<File> locateTool (String name)
    {
        return getPath().map(path -> Metal.toExecutableFile(path,name));
    }
}
