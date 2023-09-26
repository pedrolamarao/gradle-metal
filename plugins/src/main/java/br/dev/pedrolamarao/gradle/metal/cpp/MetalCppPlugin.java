package br.dev.pedrolamarao.gradle.metal.cpp;

import br.dev.pedrolamarao.gradle.metal.base.MetalBasePlugin;
import br.dev.pedrolamarao.gradle.metal.base.MetalCapability;
import br.dev.pedrolamarao.gradle.metal.base.MetalExtension;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class MetalCppPlugin implements Plugin<Project>
{
    @Override
    public void apply (Project project)
    {
        project.getPluginManager().apply(MetalBasePlugin.class);

        final var metal = project.getExtensions().getByType(MetalExtension.class);

        final var cpp = project.getObjects().domainObjectContainer(MetalCppSources.class, name -> createCppSources(project,name));
        metal.getExtensions().add("cpp", cpp);

        final var nativeImplementation = project.getConfigurations().named("nativeImplementation");

        project.getConfigurations().create(MetalBasePlugin.INCLUDABLE_DEPENDENCIES, configuration -> {
            configuration.setCanBeConsumed(false);
            configuration.setCanBeResolved(true);
            configuration.attributes(it -> it.attribute(MetalCapability.ATTRIBUTE, MetalCapability.INCLUDABLE));
            configuration.extendsFrom(nativeImplementation.get());
        });

        project.getConfigurations().create(MetalBasePlugin.INCLUDABLE_ELEMENTS, configuration -> {
            configuration.setCanBeConsumed(true);
            configuration.setCanBeResolved(false);
            configuration.attributes(it -> it.attribute(MetalCapability.ATTRIBUTE, MetalCapability.INCLUDABLE));
        });
    }

    static MetalCppSources createCppSources (Project project, String name)
    {
        final var configurations = project.getConfigurations();
        final var layout = project.getLayout();
        final var objects = project.getObjects();
        final var providers = project.getProviders();

        final var sources = objects.sourceDirectorySet(name,name);
        sources.srcDir( layout.getProjectDirectory().dir("src/%s/cpp".formatted(name)) );

        configurations.named(MetalBasePlugin.INCLUDABLE_ELEMENTS).configure(configuration ->
        {
            configuration.getOutgoing().artifacts(providers.provider(sources::getSourceDirectories));
        });

        return new MetalCppSources(name, sources);
    }
}
