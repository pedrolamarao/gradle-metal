// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * Gradle Metal base plugin.
 */
public class MetalBasePlugin implements Plugin<Project>
{
    @Override
    public void apply (Project project)
    {
        project.getGradle().getSharedServices().registerIfAbsent("metal",MetalService.class,it -> {});

        project.getExtensions().create("metal",MetalExtension.class);
    }
}
