// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal.base;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class NativeArchivePlugin implements Plugin<Project>
{
    @Override
    public void apply (Project project)
    {
        project.getPluginManager().apply(NativeBasePlugin.class);
    }
}
