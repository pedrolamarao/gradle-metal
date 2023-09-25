package br.dev.pedrolamarao.gradle.metal.cxx;

import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ListProperty;
import org.gradle.process.ExecOperations;
import org.gradle.workers.WorkAction;
import org.gradle.workers.WorkParameters;
import org.gradle.workers.WorkerExecutor;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class IxxCompileBaseTask extends CxxCompileBaseTask
{
    @Inject
    protected abstract ObjectFactory getObjectFactory ();

    @Inject
    protected abstract WorkerExecutor getWorkerExecutor ();

    public interface ScanParameter extends WorkParameters
    {
        ListProperty<String> getCompileArgs ();

        RegularFileProperty getOutputFile ();

        RegularFileProperty getSourceFile ();
    }

    public static abstract class ScanAction implements WorkAction<ScanParameter>
    {
        @Inject
        public abstract ExecOperations getExecOperations ();

        @Override
        public void execute ()
        {
            final var parameters = getParameters();
            final var sourceFile = parameters.getSourceFile().getAsFile().get();
            final var outputFile = parameters.getOutputFile().getAsFile().get();

            // obtain P1689 dependency information from sources
            final var buffer = new ByteArrayOutputStream();
            try
            {
                final var scanArgs = new ArrayList<String>();
                scanArgs.add("clang-scan-deps");
                scanArgs.add("--format=p1689");
                scanArgs.add("--");
                scanArgs.addAll(parameters.getCompileArgs().get());
                scanArgs.add(sourceFile.toString());

                getExecOperations().exec(it -> {
                    it.setCommandLine(scanArgs);
                    it.setStandardOutput(buffer);
                });
            }
            catch (RuntimeException e) { throw e; }
            catch (Exception e) { throw new RuntimeException(e); }

            // parse P1689 dependency information
            final var sourceProvides = new ArrayList<String>();
            final var sourceRequires = new ArrayList<String>();
            final IxxModule dependencies;
            try
            {
                final var json = (Map<?,?>) new groovy.json.JsonSlurper().parse( buffer.toByteArray() );
                final var rules = (List<?>) json.get("rules");
                for (var ruleObj : rules) {
                    final var rule = (Map<?,?>) ruleObj;
                    final var provides = (List<?>) rule.get("provides");
                    if (provides != null) {
                        for (var provideObj : provides) {
                            final var provide = (Map<?, ?>) provideObj;
                            final var logicalName = provide.get("logical-name");
                            sourceProvides.add(logicalName.toString());
                        }
                    }
                    final var requires = (List<?>) rule.get("requires");
                    if (requires != null) {
                        for (var requireObj : requires) {
                            final var require = (Map<?, ?>) requireObj;
                            final var logicalName = require.get("logical-name");
                            sourceRequires.add(logicalName.toString());
                        }
                    }
                }

                dependencies = new IxxModule(sourceFile,sourceProvides,sourceRequires);
            }
            catch (RuntimeException e) { throw e; }
            catch (Exception e) { throw new RuntimeException(e); }

            // serialize IxxDependencies
            try
            {
                Files.createDirectories(outputFile.toPath().getParent());
                try (var outputStream = new ObjectOutputStream(Files.newOutputStream(outputFile.toPath()))) {
                    outputStream.writeObject(dependencies);
                    outputStream.flush();
                }
            }
            catch (RuntimeException e) { throw e; }
            catch (Exception e) { throw new RuntimeException(e); }
        }
    }

    List<IxxModule> scan () throws IOException, ClassNotFoundException
    {
        // prepare base arguments
        final var scanArgs = new ArrayList<String>();
        scanArgs.add("clang++");
        scanArgs.addAll(getCompileOptions().get());
        getHeaderDependencies().forEach(file -> scanArgs.add("--include-directory=%s".formatted(file)));
        scanArgs.add("--language=c++-module");
        scanArgs.add("--precompile");

        // discover dependencies from sources: assemble dependency files
        final var scanWorkers = getWorkerExecutor().noIsolation();
        getProject().delete(getTemporaryDir());
        for (var sourceFile : getSource()) {
            final var outputPath = getTemporaryDir().toPath().resolve( "%X/%s.deps".formatted(sourceFile.hashCode(),sourceFile.getName() ));
            scanWorkers.submit(ScanAction.class, parameter -> {
                parameter.getCompileArgs().set(scanArgs);
                parameter.getOutputFile().set(outputPath.toFile());
                parameter.getSourceFile().set(sourceFile);
            });
        }
        scanWorkers.await();

        // discover dependencies from sources: parse dependency files
        final var modules = new ArrayList<IxxModule>();
        for (var dependencyFile : getObjectFactory().fileCollection().from(getTemporaryDir()).getAsFileTree()) {
            try (var stream = Files.newInputStream(dependencyFile.toPath())) {
                final var module = (IxxModule) new ObjectInputStream(stream).readObject();
                modules.add(module);
            }
        }

        // sort sources in dependency order
        modules.sort((x, y) -> {
            for (var requires : y.requires())
                if (x.provides().contains(requires))
                    return -1;
            for (var provides : y.provides())
                if (x.requires().contains(provides))
                    return 1;
            return 0;
        });

        return modules;
    }
}
