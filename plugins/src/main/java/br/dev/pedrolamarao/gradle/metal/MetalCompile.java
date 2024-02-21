// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal;

import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputDirectory;


/**
 * Gradle Metal compile task.
 */
public interface MetalCompile
{
    /**
     * Compiler tool.
     *
     * @return property
     */
    @Input
    Property<String> getCompiler ();

    /**
     * Compiler options.
     *
     * @return property
     */
    @Input
    ListProperty<String> getOptions ();

    /**
     * Compiler output directory.
     *
     * @return property
     */
    @OutputDirectory
    DirectoryProperty getOutputDirectory ();

    /**
     * Compiler target.
     *
     * @return property
     */
    @Input
    Property<String> getTarget ();
}
