package br.dev.pedrolamarao.gradle.nativelanguage;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class NativeArchivePlugin implements Plugin<Project>
{
    @Override
    public void apply (Project project)
    {
        project.getPluginManager().apply(NativeLanguagePlugin.class);

        project.getConfigurations().create("nativeLinkElements",configuration -> {
            configuration.setCanBeConsumed(true);
            configuration.setCanBeResolved(false);
            configuration.attributes(it -> it.attribute(NativeCapability.ATTRIBUTE,NativeCapability.LINKABLE));
        });
    }
}
