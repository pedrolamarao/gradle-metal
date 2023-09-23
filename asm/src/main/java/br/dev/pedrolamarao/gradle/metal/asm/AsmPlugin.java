// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal.asm;

import br.dev.pedrolamarao.gradle.metal.base.MetalExtension;
import br.dev.pedrolamarao.gradle.metal.base.NativeBasePlugin;
import br.dev.pedrolamarao.gradle.metal.cpp.CppPlugin;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class AsmPlugin implements Plugin<Project>
{
    @Override
    public void apply (Project project)
    {
        project.getPluginManager().apply(NativeBasePlugin.class);
        project.getPluginManager().apply(CppPlugin.class);

        final var metal = project.getExtensions().getByType(MetalExtension.class);
        metal.getExtensions().create("asm", AsmExtension.class);
    }
}
