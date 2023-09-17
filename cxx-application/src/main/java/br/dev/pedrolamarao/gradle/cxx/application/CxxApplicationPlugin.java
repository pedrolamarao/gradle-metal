package br.dev.pedrolamarao.gradle.cxx.application;

import br.dev.pedrolamarao.gradle.cxx.language.CxxLanguagePlugin;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class CxxApplicationPlugin implements Plugin<Project>
{
    @Override
    public void apply (Project project)
    {
        project.getPluginManager().apply(CxxLanguagePlugin.class);
    }
}
