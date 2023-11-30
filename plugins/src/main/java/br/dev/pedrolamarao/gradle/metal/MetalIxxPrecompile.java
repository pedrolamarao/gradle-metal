// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal;

import org.gradle.api.file.Directory;
import org.gradle.api.file.RegularFileProperty;
import org.gradle.api.internal.file.FileOperations;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.provider.Provider;
import org.gradle.api.services.ServiceReference;
import org.gradle.api.tasks.CacheableTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;
import org.gradle.process.ExecOperations;
import org.gradle.workers.WorkAction;
import org.gradle.workers.WorkParameters;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@CacheableTask
public abstract class MetalIxxPrecompile extends MetalCompile
{
    // properties

    @Input
    public abstract ListProperty<String> getImportPath ();

    @Input
    public abstract ListProperty<String> getIncludePath ();

    @OutputDirectory
    public Provider<Directory> getTargetOutputDirectory ()
    {
        return getOutputDirectory().zip(getMetal().map(MetalService::getTarget),Directory::dir);
    }

    // services

    @Inject
    public abstract ExecOperations getExec ();

    @Inject
    protected abstract FileOperations getFiles ();

    /**
     * Object factory service.
     *
     * @return service
     */
    @Inject
    protected abstract ObjectFactory getObjects ();

    // task

    public MetalIxxPrecompile ()
    {
        getCompiler().convention("clang++");
    }

    /**
     * Scan worker parameters.
     */
    interface ScanParameter extends WorkParameters
    {
        /**
         * Compiler arguments.
         *
         * @return property
         */
        ListProperty<String> getOptions ();

        /**
         * Output file.
         *
         * @return property
         */
        RegularFileProperty getOutput ();

        /**
         * Source file.
         *
         * @return property
         */
        RegularFileProperty getSource ();
    }

    /**
     * Scan worker action.
     */
    static abstract class ScanAction implements WorkAction<ScanParameter>
    {
        /**
         * Exec operations service.
         *
         * @return service
         */
        @Inject
        public abstract ExecOperations getExec ();

        /**
         * Metal service.
         *
         * @return service
         */
        @ServiceReference
        public abstract Property<MetalService> getMetal ();

        public ScanAction () { }

        /**
         * {@inheritDoc}
         */
        @Override
        public void execute ()
        {
            final var scanner = getMetal().map(it -> it.locateTool("clang-scan-deps")).get();

            final var parameters = getParameters();
            final var sourceFile = parameters.getSource().getAsFile().get();
            final var outputFile = parameters.getOutput().getAsFile().get();

            // obtain P1689 dependency information from sources
            final var buffer = new ByteArrayOutputStream();
            try
            {
                final var scanArgs = new ArrayList<String>();
                scanArgs.add("--format=p1689");
                scanArgs.add("--");
                scanArgs.addAll(parameters.getOptions().get());
                scanArgs.add(sourceFile.toString());

                getExec().exec(it -> {
                    it.executable(scanner);
                    it.args(scanArgs);
                    it.setStandardOutput(buffer);
                });
            }
            catch (RuntimeException e) { throw e; }
            catch (Exception e) { throw new RuntimeException(e); }

            // parse P1689 dependency information
            final var sourceProvides = new ArrayList<String>();
            final var sourceRequires = new ArrayList<String>();
            final MetalIxxModule dependencies;
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

                dependencies = new MetalIxxModule(sourceFile,sourceProvides,sourceRequires);
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

    List<MetalIxxModule> scan () throws IOException, ClassNotFoundException
    {
        // prepare base arguments
        final var scanArgs = new ArrayList<String>();
        scanArgs.add(getCompiler().get());
        scanArgs.addAll(getOptions().get());
        getIncludePath().get().forEach(file -> scanArgs.add("--include-directory=%s".formatted(file)));
        scanArgs.add("--language=c++-module");
        scanArgs.add("--precompile");

        // discover dependencies from sources: assemble dependency files
        final var scanWorkers = getWorkers().noIsolation();
        getFiles().delete(getTemporaryDir());
        for (var sourceFile : getSource()) {
            final var outputPath = getTemporaryDir().toPath().resolve( "%X/%s.deps".formatted(sourceFile.hashCode(),sourceFile.getName() ));
            scanWorkers.submit(ScanAction.class, parameter -> {
                parameter.getOptions().set(scanArgs);
                parameter.getOutput().set(outputPath.toFile());
                parameter.getSource().set(sourceFile);
            });
        }
        scanWorkers.await();

        // discover dependencies from sources: parse dependency files
        final var modules = new ArrayList<MetalIxxModule>();
        for (var dependencyFile : getObjects().fileCollection().from(getTemporaryDir()).getAsFileTree()) {
            try (var stream = Files.newInputStream(dependencyFile.toPath())) {
                final var module = (MetalIxxModule) new ObjectInputStream(stream).readObject();
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

    @TaskAction
    public void precompile () throws Exception
    {
        final var metal = getMetal().get();

        final var compiler = metal.locateTool(getCompiler().get());
        final var target = metal.getTarget();

        final var outputDirectory = getOutputDirectory().dir(target).get().getAsFile().toPath();

        // discover dependencies from sources
        final var modules = scan();

        // remove old objects
        getFiles().delete(outputDirectory);
        Files.createDirectories(outputDirectory);

        // prepare compile arguments
        final var baseArgs = new ArrayList<String>();
        baseArgs.add("--target=%s".formatted(target));
        baseArgs.addAll(getOptions().get());
        getIncludePath().get().forEach(dir -> baseArgs.add("--include-directory=%s".formatted(dir)));
        getImportPath().get().forEach(dir -> baseArgs.add("-fprebuilt-module-path=%s".formatted(dir)));
        baseArgs.add("-fprebuilt-module-path=%s".formatted(outputDirectory));
        baseArgs.add("--precompile");
        baseArgs.add("--language=c++-module");

        // compile objects from sources
        for (var module : modules)
        {
            final var moduleName = module.provides().get(0);
            final var output = outputDirectory.resolve( moduleName.replace(":","-") + ".pcm" );

            // finish compile arguments
            final var compileArgs = new ArrayList<>(baseArgs);
            compileArgs.add("--output=%s".formatted(output));
            compileArgs.add(module.source().toString());

            getExec().exec(it -> {
                it.executable(compiler);
                it.args(compileArgs);
            });
        }
    }
}
