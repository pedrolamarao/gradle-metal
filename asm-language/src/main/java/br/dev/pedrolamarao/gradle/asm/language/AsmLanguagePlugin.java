// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.asm.language;

import br.dev.pedrolamarao.gradle.nativelanguage.NativeLanguagePlugin;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class AsmLanguagePlugin implements Plugin<Project>
{
    @Override
    public void apply (Project project)
    {
        project.getPluginManager().apply(NativeLanguagePlugin.class);
    }
}
