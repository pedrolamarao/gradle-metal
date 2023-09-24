package br.dev.pedrolamarao.gradle.metal.c;

import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.SourceTask;

public abstract class CCompileBaseTask extends SourceTask
{
    @InputFiles
    public abstract ConfigurableFileCollection getHeaderDependencies ();

    @Input
    public abstract ListProperty<String> getCompileOptions ();

    @OutputDirectory
    public abstract DirectoryProperty getOutputDirectory ();
}
