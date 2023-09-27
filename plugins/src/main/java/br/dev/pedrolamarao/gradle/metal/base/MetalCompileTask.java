package br.dev.pedrolamarao.gradle.metal.base;

import org.gradle.api.file.Directory;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.OutputDirectory;

import java.nio.file.Path;

public abstract class MetalCompileTask extends MetalSourceTask
{
    @Input
    public abstract ListProperty<String> getCompileOptions ();

    @Internal
    public abstract DirectoryProperty getOutputDirectory ();

    @OutputDirectory
    public Provider<Directory> getTargetDirectory ()
    {
        return getOutputDirectory().flatMap(it -> it.dir(getTarget().orElse("default")));
    }

    protected static Path toOutputPath (Path base, Path source, Path output, String extension)
    {
        final var hash = hash(base.relativize(source));
        final var name = source.getFileName() + extension;
        return output.resolve("%X/%s".formatted(hash,name));
    }

    // see: https://en.wikipedia.org/wiki/Fowler–Noll–Vo_hash_function

    static final int FNV_OFFSET_32 = 0x811c9dc5;

    static final int FNV_PRIME_32 = 0x01000193;

    static int hash (byte[] bytes)
    {
        int hash = FNV_OFFSET_32;
        for (byte b : bytes) {
            hash = hash * FNV_PRIME_32;
            hash = hash ^ b;
        }
        return hash;
    }

    static int hash (String string)
    {
        return hash(string.getBytes());
    }

    static int hash (Path path)
    {
        return hash(path.toString().getBytes());
    }
}
