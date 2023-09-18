package br.dev.pedrolamarao.gradle.cxx.language;

import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.workers.WorkParameters;

public abstract class CxxCompileWorkParameters implements WorkParameters
{
    public abstract RegularFileProperty getSource ();

    public abstract ListProperty<String> getOptions ();

    public abstract RegularFileProperty getOutput ();

    public abstract Property<String> getTarget ();

}
