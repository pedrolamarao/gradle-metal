// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal.c;

import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.workers.WorkParameters;

public abstract class CCompileWorkParameters implements WorkParameters
{
    public abstract ConfigurableFileCollection getHeaderDependencies ();

    public abstract ListProperty<String> getOptions ();

    public abstract RegularFileProperty getOutput ();

    public abstract RegularFileProperty getSource ();

}
