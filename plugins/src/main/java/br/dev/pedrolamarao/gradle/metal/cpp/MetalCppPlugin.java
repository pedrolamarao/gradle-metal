package br.dev.pedrolamarao.gradle.metal.cpp;

import br.dev.pedrolamarao.gradle.metal.base.Metal;
import br.dev.pedrolamarao.gradle.metal.base.MetalBasePlugin;
import br.dev.pedrolamarao.gradle.metal.base.MetalExtension;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * C preprocessor support plugin.
 */
public class MetalCppPlugin implements Plugin<Project>
{
    /**
     * {@inheritDoc}
     */
    @Override
    public void apply (Project project)
    {
        project.getPluginManager().apply(MetalBasePlugin.class);

        final var metal = project.getExtensions().getByType(MetalExtension.class);

        final var cpp = project.getObjects().domainObjectContainer(MetalCppSources.class, name -> createCppSources(project,name));
        metal.getExtensions().add("cpp", cpp);
    }

    static MetalCppSources createCppSources (Project project, String name)
    {
        final var configurations = project.getConfigurations();
        final var layout = project.getLayout();
        final var objects = project.getObjects();
        final var providers = project.getProviders();

        final var sourceSet = objects.newInstance(MetalCppSources.class,name);
        sourceSet.getSources().from( layout.getProjectDirectory().dir("src/%s/cpp".formatted(name)) );

        project.afterEvaluate(it ->
        {
            if (sourceSet.getPublic().get()) {
                configurations.named(Metal.INCLUDABLE_ELEMENTS).configure(configuration -> {
                    configuration.getOutgoing().artifacts(providers.provider(sourceSet::getSources));
                });
            }
        });

        return sourceSet;
    }
}
