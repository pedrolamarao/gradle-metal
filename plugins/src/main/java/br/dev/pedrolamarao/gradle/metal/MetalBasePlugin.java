package br.dev.pedrolamarao.gradle.metal;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class MetalBasePlugin implements Plugin<Project>
{
    @Override
    public void apply (Project project)
    {
        project.getGradle().getSharedServices().registerIfAbsent("metal",MetalService.class,it -> {});

        project.getExtensions().create("metal",MetalExtension.class);
    }
}
