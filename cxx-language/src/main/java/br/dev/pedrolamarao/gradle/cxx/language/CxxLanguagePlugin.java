package br.dev.pedrolamarao.gradle.cxx.language;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class CxxLanguagePlugin implements Plugin<Project>
{
    @Override
    public void apply (Project project)
    {
        project.getExtensions().create("cxx",CxxExtension.class,project.getObjects());
    }
}
