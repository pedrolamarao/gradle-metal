// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal.base;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class NativeBasePlugin implements Plugin<Project>
{
    @Override
    public void apply (Project project)
    {
        final var extension = project.getExtensions().create("metal",MetalExtension.class);

        final var configurations = project.getConfigurations();

        configurations.create(MetalConfigurations.COMMANDS_ELEMENTS,configuration ->
        {
            configuration.setCanBeConsumed(true);
            configuration.setCanBeResolved(false);
            configuration.attributes(it -> it.attribute(NativeCapability.ATTRIBUTE,NativeCapability.COMMANDS));
        });

        final var nativeImplementation = configurations.create("nativeImplementation",configuration -> {
            configuration.setCanBeConsumed(true);
            configuration.setCanBeResolved(false);
        });

        configurations.create("nativeNoElements",configuration -> {
            configuration.attributes(it -> it.attribute(NativeCapability.ATTRIBUTE,NativeCapability.NONE));
            configuration.setCanBeConsumed(true);
            configuration.setCanBeDeclared(false);
            configuration.setCanBeResolved(false);
            configuration.setVisible(false);
        });

        configurations.create("nativeLinkDependencies", configuration -> {
            configuration.setCanBeConsumed(false);
            configuration.setCanBeResolved(true);
            configuration.attributes(it -> it.attribute(NativeCapability.ATTRIBUTE,NativeCapability.LINKABLE));
            configuration.extendsFrom(nativeImplementation);
        });

        configurations.create("nativeLinkElements",configuration -> {
            configuration.setCanBeConsumed(true);
            configuration.setCanBeResolved(false);
            configuration.attributes(it -> it.attribute(NativeCapability.ATTRIBUTE,NativeCapability.LINKABLE));
        });

        project.getDependencies().getAttributesSchema().attribute(NativeCapability.ATTRIBUTE, it -> {
            it.getCompatibilityRules().add(NativeCapabilityCompatibilityRule.class);
        });
    }
}