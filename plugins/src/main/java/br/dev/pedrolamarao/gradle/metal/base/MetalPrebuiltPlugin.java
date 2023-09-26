package br.dev.pedrolamarao.gradle.metal.base;

import br.dev.pedrolamarao.gradle.metal.cpp.MetalCppPlugin;
import org.gradle.api.Action;
import org.gradle.api.NonNullApi;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.artifacts.ConfigurablePublishArtifact;
import org.gradle.api.artifacts.ConfigurationContainer;

import javax.inject.Inject;

public class MetalPrebuiltPlugin implements Plugin<Project>
{
    @NonNullApi
    public abstract static class Extension
    {
        @Inject
        protected abstract ConfigurationContainer getConfigurations ();

        @Inject
        public Extension () { }

        public void includable (Object notation)
        {
            getConfigurations().named(MetalCppPlugin.CPP_INCLUDABLE_ELEMENTS).configure(configuration -> {
                configuration.getOutgoing().artifact(notation);
            });
        }

        public void includable (Object notation, Action<? super ConfigurablePublishArtifact> configure)
        {
            getConfigurations().named(MetalCppPlugin.CPP_INCLUDABLE_ELEMENTS).configure(configuration -> {
                configuration.getOutgoing().artifact(notation, configure);
            });
        }

        public void linkable (Object notation)
        {
            getConfigurations().named("nativeLinkElements").configure(configuration ->
            {
                configuration.getOutgoing().artifact(notation);
            });
        }

        public void linkable (Object notation, Action<? super ConfigurablePublishArtifact> configure)
        {
            getConfigurations().named("nativeLinkElements").configure(configuration -> {
                configuration.getOutgoing().artifact(notation, configure);
            });
        }
    }

    @Override
    public void apply (Project project)
    {
        project.getPluginManager().apply(MetalBasePlugin.class);
        project.getExtensions().getByType(MetalExtension.class).getExtensions().create("prebuilt", Extension.class);
    }
}
