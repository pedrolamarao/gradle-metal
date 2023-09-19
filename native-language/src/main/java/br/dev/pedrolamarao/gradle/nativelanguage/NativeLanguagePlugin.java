// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.nativelanguage;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class NativeLanguagePlugin implements Plugin<Project>
{
    @Override
    public void apply (Project project)
    {
        project.getConfigurations().create("nativeNoElements",configuration -> {
            configuration.attributes(it -> it.attribute(NativeCapability.ATTRIBUTE,NativeCapability.NONE));
            configuration.setCanBeConsumed(true);
            configuration.setCanBeDeclared(false);
            configuration.setCanBeResolved(false);
            configuration.setVisible(false);
        });

        project.getConfigurations().create("nativeImplementation",configuration -> {
            configuration.setCanBeConsumed(true);
            configuration.setCanBeResolved(false);
        });

        project.getDependencies().getAttributesSchema().attribute(NativeCapability.ATTRIBUTE, it -> {
            it.getCompatibilityRules().add(NativeCapabilityCompatibilityRule.class);
        });
    }
}