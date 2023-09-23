// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal.cpp;

import org.gradle.api.file.FileCollection;
import org.gradle.api.file.SourceDirectorySet;

import javax.inject.Inject;

public class CppSources
{
    final SourceDirectorySet sources;

    public CppSources (SourceDirectorySet sources)
    {
        this.sources = sources;
    }

    public SourceDirectorySet getSources ()
    {
        return sources;
    }
}
