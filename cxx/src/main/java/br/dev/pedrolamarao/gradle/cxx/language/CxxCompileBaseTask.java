package br.dev.pedrolamarao.gradle.cxx.language;

import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.SourceTask;

public abstract class CxxCompileBaseTask extends SourceTask
{
    @InputFiles
    public abstract ConfigurableFileCollection getHeaderDependencies ();

    @InputFiles
    public abstract ConfigurableFileCollection getModuleDependencies ();

    @OutputDirectory
    public abstract DirectoryProperty getOutputDirectory ();
}
