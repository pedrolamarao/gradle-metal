// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.asm.language;

import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.workers.WorkParameters;

public abstract class AsmCompileWorkParameters implements WorkParameters
{
    public abstract DirectoryProperty getBaseDirectory ();

    public abstract ListProperty<String> getOptions ();

    public abstract DirectoryProperty getOutputDirectory ();

    public abstract RegularFileProperty getSourceFile ();

    public abstract Property<String> getTargetMachine ();
}
