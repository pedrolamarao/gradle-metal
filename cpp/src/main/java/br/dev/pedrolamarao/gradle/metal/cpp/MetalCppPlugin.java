package br.dev.pedrolamarao.gradle.metal.cpp;

import br.dev.pedrolamarao.gradle.metal.base.MetalExtension;
import br.dev.pedrolamarao.gradle.metal.base.NativeBasePlugin;
import br.dev.pedrolamarao.gradle.metal.base.NativeCapability;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

import java.util.function.Function;

public class MetalCppPlugin implements Plugin<Project>
{
    public static final Function<String,String> CPP_INCLUDABLES = name -> "%s-includables".formatted(name);

    public static final String CPP_INCLUDABLE_ELEMENTS = "cppIncludableElements";

    public static final String CPP_INCLUDABLE_DEPENDENCIES = "cppIncludableDependencies";

    @Override
    public void apply (Project project)
    {
        project.getPluginManager().apply(NativeBasePlugin.class);

        final var metal = project.getExtensions().getByType(MetalExtension.class);

        final var cpp = project.getObjects().domainObjectContainer(MetalCppSources.class, name -> createCppSources(project,name));
        metal.getExtensions().create("cpp", MetalCppExtension.class, cpp);

        final var nativeImplementation = project.getConfigurations().named("nativeImplementation");

        project.getConfigurations().create(CPP_INCLUDABLE_DEPENDENCIES, configuration -> {
            configuration.setCanBeConsumed(false);
            configuration.setCanBeResolved(true);
            configuration.attributes(it -> it.attribute(NativeCapability.ATTRIBUTE,NativeCapability.INCLUDABLE));
            configuration.extendsFrom(nativeImplementation.get());
        });

        project.getConfigurations().create(CPP_INCLUDABLE_ELEMENTS, configuration -> {
            configuration.setCanBeConsumed(true);
            configuration.setCanBeResolved(false);
            configuration.attributes(it -> it.attribute(NativeCapability.ATTRIBUTE,NativeCapability.INCLUDABLE));
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

        configurations.named(CPP_INCLUDABLE_ELEMENTS).configure(configuration ->
        {
            configuration.getOutgoing().artifacts(providers.provider(sources::getSourceDirectories));
        });

        final var includables = configurations.create(CPP_INCLUDABLES.apply(name),configuration ->
        {
            configuration.setCanBeConsumed(true);
            configuration.setCanBeResolved(false);
            configuration.setVisible(false);
            configuration.getOutgoing().artifacts(providers.provider(sources::getSourceDirectories));
        });

        return new MetalCppSources(name, sources);
    }
}
