package br.dev.pedrolamarao.gradle.cxx.language;

import org.gradle.api.file.*;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.workers.WorkParameters;

public abstract class CxxCompileWorkParameters implements WorkParameters
{
    public abstract ListProperty<String> getOptions ();

    public abstract RegularFileProperty getOutput ();

    public abstract RegularFileProperty getSource ();

}
