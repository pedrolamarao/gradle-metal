package br.dev.pedrolamarao.gradle.metal.base;

import org.gradle.api.GradleException;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.api.provider.ValueSource;
import org.gradle.api.provider.ValueSourceParameters;
import org.gradle.process.ExecOperations;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;

/**
 * Provides the host target.
 *
 * <p>
 *     Discovers the host target by invoking the compiler.
 * </p>
 */
public abstract class MetalHostTargetSource implements ValueSource<String, ValueSourceParameters.None>
{
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
     * {@inheritDoc}
     */
    @Nullable
    @Override
    public String obtain ()
    {
        final var path = getProviders().gradleProperty("metal.path")
            .orElse(getProviders().environmentVariable("PATH"))
            .orElse("");

        final var file = Metal.toExecutableFile(path.get(),"clang");

        final var buffer = new ByteArrayOutputStream();
        getExec().exec(exec -> {
            exec.executable(file.toString());
            exec.args("-v");
            exec.setErrorOutput(buffer);
        });

        try (var reader = new BufferedReader( new StringReader( buffer.toString() ) ) )
        {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Target")) {
                    final var split = line.split(":");
                    if (split.length != 2) continue; // TODO: unexpected clang -v output
                    final var part = split[1];
                    return part.trim();
                }
            }
        }
        catch (IOException e) { throw new GradleException("failed parsing clang -v output", e); }

        throw new GradleException("failed to discover host target");
    }
}
