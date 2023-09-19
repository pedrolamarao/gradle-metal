// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal.asm;

import br.dev.pedrolamarao.gradle.nativelanguage.NativeLanguagePlugin;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class AsmPlugin implements Plugin<Project>
{
    @Override
    public void apply (Project project)
    {
        project.getPluginManager().apply(NativeLanguagePlugin.class);

        project.getExtensions().create("asm", AsmExtension.class);
    }
}
