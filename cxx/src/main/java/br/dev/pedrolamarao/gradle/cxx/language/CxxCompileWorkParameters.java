// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.cxx.language;

import org.gradle.api.file.*;
import org.gradle.api.provider.ListProperty;
import org.gradle.workers.WorkParameters;

public abstract class CxxCompileWorkParameters implements WorkParameters
{
    public abstract ConfigurableFileCollection getHeaderDependencies ();

    public abstract ConfigurableFileCollection getModuleDependencies ();

    public abstract ListProperty<String> getOptions ();

    public abstract RegularFileProperty getOutput ();

    public abstract RegularFileProperty getSource ();

}
