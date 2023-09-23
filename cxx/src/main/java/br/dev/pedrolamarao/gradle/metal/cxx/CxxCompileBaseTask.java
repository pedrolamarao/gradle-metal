// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal.cxx;

import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.SourceTask;

public abstract class CxxCompileBaseTask extends SourceTask
{
    @InputFiles
    public abstract ConfigurableFileCollection getHeaderDependencies ();

    @InputFiles
    public abstract ConfigurableFileCollection getModuleDependencies ();

    @Input
    public abstract ListProperty<String> getCompileOptions ();

    @OutputDirectory
    public abstract DirectoryProperty getOutputDirectory ();
}
