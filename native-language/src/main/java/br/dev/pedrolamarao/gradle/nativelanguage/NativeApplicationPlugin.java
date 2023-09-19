package br.dev.pedrolamarao.gradle.nativelanguage;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class NativeApplicationPlugin implements Plugin<Project>
{
    @Override
    public void apply (Project project)
    {
        project.getPluginManager().apply(NativeLanguagePlugin.class);

        final var nativeImplementation = project.getConfigurations().named("nativeImplementation");

        project.getConfigurations().create("nativeLinkDependencies", configuration -> {
            configuration.setCanBeConsumed(false);
            configuration.setCanBeResolved(true);
            configuration.attributes(it -> it.attribute(NativeCapability.ATTRIBUTE,NativeCapability.LINKABLE));
            configuration.extendsFrom(nativeImplementation.get());
        });
    }
}
