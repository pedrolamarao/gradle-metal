package br.dev.pedrolamarao.gradle.metal.base;

import org.gradle.api.GradleException;
import org.gradle.api.provider.Provider;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.api.services.BuildService;
import org.gradle.api.services.BuildServiceParameters;
import org.gradle.process.ExecOperations;

import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Metal service.
 */
public abstract class MetalService implements BuildService<BuildServiceParameters.None>
{
    private final AtomicReference<String> host = new AtomicReference<>();

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
     * Host name.
     *
     * @return value
     */
    public Provider<String> getHost ()
    {
        return getProviders().provider(host::get).orElse(
            getProviders().exec(exec ->
            {
                exec.executable( Metal.toExecutableFile(path,"clang") );
                exec.args("-v");
            })
            .getStandardError().getAsText().map(buffer ->
            {
                String value = "unknown";
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
                host.set(value);
                return value;
            })
        );
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
        return executableFileName(getHost().get(),name);
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
