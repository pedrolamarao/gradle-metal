// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal.c;

import br.dev.pedrolamarao.gradle.metal.base.MetalExtension;
import br.dev.pedrolamarao.gradle.metal.base.NativeBasePlugin;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class CPlugin implements Plugin<Project>
{
    @Override
    public void apply (Project project)
    {
        project.getPluginManager().apply(NativeBasePlugin.class);

        final var metal = project.getExtensions().getByType(MetalExtension.class);
        metal.getExtensions().create("c",CExtension.class);
    }
}
