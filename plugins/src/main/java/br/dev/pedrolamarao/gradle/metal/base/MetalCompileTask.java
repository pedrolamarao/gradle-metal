package br.dev.pedrolamarao.gradle.metal.base;

import org.gradle.api.file.Directory;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Provider;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.workers.WorkerExecutor;

import javax.inject.Inject;
import java.nio.file.Path;

/**
 * Compile sources task.
 */
public abstract class MetalCompileTask extends MetalSourceTask
{
    /**
     * Compile options.
     *
     * @return property
     */
    @Input
    public abstract ListProperty<String> getCompileOptions ();

    /**
     * Outputs base directory.
     *
     * @return property
     */
    @Internal
    public abstract DirectoryProperty getOutputDirectory ();

    /**
     * Outputs target directory.
     *
     * @return provider
     */
    @OutputDirectory
    public Provider<Directory> getTargetDirectory ()
    {
        return getOutputDirectory().flatMap(out -> getTarget().map(out::dir));
    }

    /**
     * Worker executor service.
     *
     * @return service
     */
    @Inject
    public abstract WorkerExecutor getWorkers ();

    /**
     * Generate output path.
     *
     * @param base       source base directory
     * @param source     source file
     * @param output     output base directory
     * @param extension  output file extension
     * @return output path
     */
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
