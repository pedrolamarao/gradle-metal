// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal.c;

import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public abstract class CCompileOptions
{
    public abstract Property<String> getLanguageDialect ();

    public abstract ListProperty<File> getIncludePath ();

    public abstract Property<String> getTargetMachine ();

    public List<String> toList ()
    {
        final var list = new ArrayList<String>();
        if (getTargetMachine().isPresent()) list.add("--target=%s".formatted(getTargetMachine().get()));
        getIncludePath().get().forEach(file -> list.add("--include-directory=%s".formatted(file)));
        if (getLanguageDialect().isPresent()) list.add("--std=%s".formatted(getLanguageDialect().get()));
        return list;
    }
}
