// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal.cxx;

import br.dev.pedrolamarao.gradle.metal.base.MetalExtension;
import br.dev.pedrolamarao.gradle.metal.base.NativeCapability;
import br.dev.pedrolamarao.gradle.metal.base.NativeBasePlugin;
import br.dev.pedrolamarao.gradle.metal.cpp.CppPlugin;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class CxxPlugin implements Plugin<Project>
{
    @Override
    public void apply (Project project)
    {
        project.getPluginManager().apply(NativeBasePlugin.class);
        project.getPluginManager().apply(CppPlugin.class);

        final var metal = project.getExtensions().getByType(MetalExtension.class);
        metal.getExtensions().create("cxx",CxxExtension.class);
        metal.getExtensions().create("ixx",IxxExtension.class);

        final var nativeImplementation = project.getConfigurations().named("nativeImplementation");

        project.getConfigurations().create("cxxImportDependencies", configuration -> {
            configuration.setCanBeConsumed(false);
            configuration.setCanBeResolved(true);
            configuration.attributes(it -> it.attribute(NativeCapability.ATTRIBUTE,NativeCapability.IMPORTABLE));
            configuration.extendsFrom(nativeImplementation.get());
        });

        project.getConfigurations().create("cxxImportElements", configuration -> {
            configuration.setCanBeConsumed(true);
            configuration.setCanBeResolved(false);
            configuration.attributes(it -> it.attribute(NativeCapability.ATTRIBUTE,NativeCapability.IMPORTABLE));
        });

    }
}
