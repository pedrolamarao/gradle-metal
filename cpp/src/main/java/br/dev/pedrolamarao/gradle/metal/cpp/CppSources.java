package br.dev.pedrolamarao.gradle.metal.cpp;

import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.tasks.TaskProvider;

import javax.inject.Inject;

public abstract class CppSources
{
    final SourceDirectorySet sourceDirectorySet;

    @Inject
    public CppSources (SourceDirectorySet sourceDirectorySet)
    {
        this.sourceDirectorySet = sourceDirectorySet;
    }

    public SourceDirectorySet getSourceDirectories ()
    {
        return sourceDirectorySet;
    }
}
