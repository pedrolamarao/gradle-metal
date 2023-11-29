package br.dev.pedrolamarao.gradle.metal;

import org.gradle.api.GradleException;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.ValueSource;
import org.gradle.api.provider.ValueSourceParameters;
import org.gradle.process.ExecOperations;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;

public abstract class MetalHostValueSource implements ValueSource<String,MetalHostValueSource.Parameters>
{
    public interface Parameters extends ValueSourceParameters
    {
        Property<String> getPath ();
    }

    @Inject
    protected abstract ExecOperations getExec ();

    private String value = null;

    @Nullable
    @Override
    public String obtain ()
    {
        if (value != null) return value;

        final var path = getParameters().getPath().get();

        final var buffer = new ByteArrayOutputStream();
        getExec().exec(exec -> {
            exec.executable( Metal.toExecutableFile(path,"clang") );
            exec.args("-v");
            exec.setErrorOutput(buffer);
        });

        value = "unknown";
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

        return value;
    }
}
