package br.dev.pedrolamarao.gradle.metal.base;

import org.gradle.api.provider.Provider;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.api.services.BuildService;
import org.gradle.api.services.BuildServiceParameters;

import javax.inject.Inject;

/**
 * Metal service.
 */
public abstract class MetalService implements BuildService<BuildServiceParameters.None>
{
    private final Provider<String> hostTarget;

    /**
     * Constructor.
     *
     * @param providers  provider factory
     */
    @Inject
    public MetalService (ProviderFactory providers)
    {
        hostTarget = providers.of(MetalHostTargetSource.class, it -> {});
    }

    /**
     * Host target.
     *
     * @return provider
     */
    public Provider<String> getHostTarget ()
    {
        return hostTarget;
    }
}
