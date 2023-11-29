// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal;

import org.gradle.api.provider.Provider;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.api.services.BuildService;
import org.gradle.api.services.BuildServiceParameters;

import javax.inject.Inject;
import java.io.File;

/**
 * Metal service.
 */
public abstract class MetalService implements BuildService<BuildServiceParameters.None>
{
    private final Provider<String> host;

    private final Provider<String> path;

    private final Provider<String> target;

    /**
     * Constructor.
     */
    @Inject
    public MetalService ()
    {
        path = getProviders().gradleProperty("metal.path")
            .orElse( getProviders().environmentVariable("PATH") )
            .orElse("");

        host = getProviders().of(MetalHostValueSource.class,spec ->
            spec.parameters(it -> it.getPath().set(path))
        );

        target = getProviders().gradleProperty("metal.target")
            .orElse( getHost() );
    }

    /**
     * Provider factory service.
     *
     * @return service
     */
    @Inject
    protected abstract ProviderFactory getProviders ();

    /**
     * Host name.
     *
     * @return value
     */
    public Provider<String> getHost ()
    {
        return host;
    }

    /**
     * Tools path.
     *
     * @return value
     */
    public String getPath () { return path.get(); }

    /**
     * Target name.
     *
     * @return value
     */
    public String getTarget ()
    {
        return target.get();
    }

    /**
     * Locate tool.
     *
     * @param name  tool name
     * @return      tool executable file provider
     */
    public File locateTool (String name)
    {
        return path.map(path -> Metal.toExecutableFile(path,name)).get();
    }
}
