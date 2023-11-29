// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal;

import org.gradle.api.GradleException;
import org.gradle.api.provider.Provider;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.api.services.BuildService;
import org.gradle.api.services.BuildServiceParameters;

import javax.inject.Inject;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Metal service.
 */
public abstract class MetalService implements BuildService<BuildServiceParameters.None>
{
    private final String path;

    private final String target;

    /**
     * Constructor.
     */
    @Inject
    public MetalService ()
    {
        path = getProviders().gradleProperty("metal.path")
            .orElse( getProviders().environmentVariable("PATH") )
            .orElse("")
            .get();
        target = getProviders().gradleProperty("metal.target")
            .orElse( getHost() )
            .get();
    }

    /**
     * Provider factory service.
     *
     * @return service
     */
    @Inject
    protected abstract ProviderFactory getProviders ();

    /**
     * Host name.
     *
     * @return value
     */
    public Provider<String> getHost ()
    {
        return getProviders().of(MetalHostValueSource.class,spec -> spec.parameters(it -> it.getPath().set(getPath())));
    }

    /**
     * Tools path.
     *
     * @return value
     */
    public String getPath () { return path; }

    /**
     * Target name.
     *
     * @return value
     */
    public String getTarget ()
    {
        return target;
    }

    /**
     * Formats an archive file name according to the host convention.
     *
     * @param name  core name
     * @return      file name
     */
    public String archiveFileName (String name)
    {
        return archiveFileName(getHost().get(),name);
    }

    /**
     * Formats an archive file name according to the target convention.
     *
     * @param target  target
     * @param name    core name
     * @return        file name
     */
    public static String archiveFileName (String target, String name)
    {
        final var prefix = archiveFilePrefix(target);
        final var suffix = archiveFileSuffix(target);
        return "%s%s%s".formatted(prefix,name,suffix);
    }

    static String archiveFilePrefix (String target)
    {
        final var parts = target.split("-");
        if (contains(parts,"msvc")) return "";
        else return "lib";
    }

    static String archiveFileSuffix (String target)
    {
        final var parts = target.split("-");
        if (contains(parts,"msvc")) return ".lib";
        else return ".a";
    }

    /**
     * Formats an executable file name according to the host convention.
     *
     * @param name  core name
     * @return      file name
     */
    public String executableFileName (String name)
    {
        return executableFileName(getHost().get(),name);
    }

    /**
     * Formats an executable file name according to the target convention.
     *
     * @param target  target
     * @param name    core name
     * @return        file name
     */
    public static String executableFileName (String target, String name)
    {
        final var prefix = executableFilePrefix(target);
        final var suffix = executableFileSuffix(target);
        return "%s%s%s".formatted(prefix,name,suffix);
    }

    static String executableFilePrefix (String target)
    {
        return "";
    }

    static String executableFileSuffix (String target)
    {
        final var parts = target.split("-");
        if (contains(parts,"windows")) return ".exe";
        else return "";
    }

    /**
     * Locate tool.
     *
     * @param name  tool name
     * @return      tool executable file provider
     */
    public File locateTool (String name)
    {
        for (var item : path.split(File.pathSeparator))
        {
            final var directory = Paths.get(item);
            if (! Files.isDirectory(directory)) continue;
            final var file = directory.resolve(name);
            if (Files.isExecutable(file)) return file.toFile();
            final var file_exe = file.resolveSibling(name + ".exe");
            if (Files.isExecutable(file_exe)) return file_exe.toFile();
        }

        throw new GradleException("executable file not found: " + name);
    }

    static <T> boolean contains (T[] array, T value)
    {
        for (T item : array)
            if (item.equals(value))
                return true;
        return false;
    }
}
