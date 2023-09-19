// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal.cxx;

import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public abstract class CxxCompileOptions
{
    public abstract Property<String> getLanguageDialect ();

    public abstract ListProperty<File> getIncludePath ();

    public abstract ListProperty<File> getModules ();

    public abstract ListProperty<File> getModulePath ();

    public abstract Property<String> getTargetMachine ();

    public List<String> toList ()
    {
        final var list = new ArrayList<String>();
        if (getTargetMachine().isPresent()) list.add("--target=%s".formatted(getTargetMachine().get()));
        getIncludePath().get().forEach(file -> list.add("-I%s".formatted(file)));
        if (getLanguageDialect().isPresent()) list.add("-std=%s".formatted(getLanguageDialect().get()));
        return list;
    }
}
