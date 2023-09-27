// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal.cxx;

import br.dev.pedrolamarao.gradle.metal.base.MetalHash;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.SourceTask;

import java.nio.file.Path;

public abstract class MetalCxxCompileBaseTask extends SourceTask
{
    @InputFiles
    public abstract ConfigurableFileCollection getIncludables ();

    @InputFiles
    public abstract ConfigurableFileCollection getImportables ();

    @Input
    public abstract ListProperty<String> getCompileOptions ();

    protected static Path toOutputPath (Path base, Path source, Path output, String extension)
    {
        final var p0 = base.relativize(source);
        final var p1 = output.resolve("%X".formatted(MetalHash.hash(p0)));
        return p1.resolve(source.getFileName() + extension);
    }
}
