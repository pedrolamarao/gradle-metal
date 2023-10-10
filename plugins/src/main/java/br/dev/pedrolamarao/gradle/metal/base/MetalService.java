package br.dev.pedrolamarao.gradle.metal.base;

import org.gradle.api.GradleException;
import org.gradle.api.provider.Provider;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.api.services.BuildService;
import org.gradle.api.services.BuildServiceParameters;
import org.gradle.process.ExecOperations;

import javax.inject.Inject;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Metal service.
 */
public abstract class MetalService implements BuildService<BuildServiceParameters.None>
{
    private final AtomicReference<String> hostTarget = new AtomicReference<>();

    private final String path;

    /**
     * Constructor.
     */
    @Inject
    public MetalService ()
    {
        path = getProviders().gradleProperty("metal.path")
            .orElse(getProviders().environmentVariable("PATH"))
            .orElse("")
            .get();
    }

    /**
     * Exec operations service.
     *
     * @return service
     */
    @Inject
    protected abstract ExecOperations getExec ();

    /**
     * Provider factory service.
     *
     * @return service
     */
    @Inject
    protected abstract ProviderFactory getProviders ();

    /**
     * Host target.
     *
     * @return target name
     */
    public String getHost ()
    {
        final var cached = hostTarget.get();
        if (cached != null) return cached;

        final var buffer = new ByteArrayOutputStream();
        getExec().exec(exec -> {
            exec.executable( Metal.toExecutableFile(path,"clang") );
            exec.args("-v");
            exec.setErrorOutput(buffer);
        });

        String value = null;
        try (var reader = new BufferedReader( new StringReader( buffer.toString() ) ) )
        {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Target")) {
                    final var split = line.split(":");
                    if (split.length != 2) continue;
                    value = split[1].trim();
                    break;
                }
            }
        }
        catch (IOException e) { throw new GradleException("failed parsing clang -v output", e); }

        if (value != null) {
            hostTarget.set(value);
            return value;
        }

        throw new GradleException("failed to discover host target");
    }

    /**
     * Formats an archive file name according to the host convention.
     *
     * @param name  core name
     * @return      file name
     */
    public String archiveFileName (String name)
    {
        return archiveFileName(getHost(),name);
    }

    /**
     * Formats an archive file name according to the target convention.
     *
     * @param target  target
     * @param name    core name
     * @return        file name
     */
    public String archiveFileName (String target, String name)
    {
        final var prefix = archiveFilePrefix(target);
        final var suffix = archiveFileSuffix(target);
        return "%s%s%s".formatted(prefix,name,suffix);
    }

    String archiveFilePrefix (String target)
    {
        final var parts = target.split("-");
        if (contains(parts,"msvc")) return "";
        else return "lib";
    }

    String archiveFileSuffix (String target)
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
        return executableFileName(getHost(),name);
    }

    /**
     * Formats an executable file name according to the target convention.
     *
     * @param target  target
     * @param name    core name
     * @return        file name
     */
    public String executableFileName (String target, String name)
    {
        final var prefix = executableFilePrefix(target);
        final var suffix = executableFileSuffix(target);
        return "%s%s%s".formatted(prefix,name,suffix);
    }

    String executableFilePrefix (String target)
    {
        return "";
    }

    String executableFileSuffix (String target)
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
    public Provider<File> locateTool (String name)
    {
        for (var item : path.split(File.pathSeparator))
        {
            final var directory = Paths.get(item);
            if (! Files.isDirectory(directory)) continue;
            final var file = directory.resolve(name);
            if (Files.isExecutable(file)) return getProviders().provider(file::toFile);
            final var file_exe = file.resolveSibling(name + ".exe");
            if (Files.isExecutable(file_exe)) return getProviders().provider(file_exe::toFile);
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
