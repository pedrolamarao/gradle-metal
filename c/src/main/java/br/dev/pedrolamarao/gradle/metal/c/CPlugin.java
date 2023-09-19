// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal.c;

import br.dev.pedrolamarao.gradle.nativelanguage.NativeLanguagePlugin;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class CPlugin implements Plugin<Project>
{
    @Override
    public void apply (Project target)
    {
        target.getPluginManager().apply(NativeLanguagePlugin.class);
        target.getExtensions().create("c",CExtension.class);
    }
}
