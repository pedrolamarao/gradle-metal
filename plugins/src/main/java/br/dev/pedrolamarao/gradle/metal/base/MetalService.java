package br.dev.pedrolamarao.gradle.metal.base;

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
    private final Provider<String> hostTarget;

    private final Provider<String> path;

    /**
     * Constructor.
     */
    @Inject
    public MetalService ()
    {
        hostTarget = getProviders().of(MetalHostTargetSource.class, it -> {});
        path = getProviders().gradleProperty("metal.path")
            .orElse(getProviders().environmentVariable("PATH"));
    }

    /**
     * Provider factory service.
     *
     * @return service
     */
    @Inject
    protected abstract ProviderFactory getProviders ();

    /**
     * Host target.
     *
     * @return provider
     */
    public Provider<String> getHostTarget ()
    {
        return hostTarget;
    }

    /**
     * Locate tool.
     *
     * @param name  tool name
     * @return      tool executable file provider
     */
    public Provider<File> locateTool (String name)
    {
        return path.map(it -> Metal.toExecutableFile(it,name));
    }
}
