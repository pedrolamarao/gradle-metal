package br.dev.pedrolamarao.gradle.metal.base;

import org.gradle.api.internal.file.FileOperations;
import org.gradle.api.provider.Property;
import org.gradle.api.services.ServiceReference;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.SourceTask;
import org.gradle.process.ExecOperations;

import javax.inject.Inject;
import java.io.File;

/**
 * Metal source task.
 */
public abstract class MetalSourceTask extends SourceTask
{
    /**
     * Source base directory.
     *
     * <p>Used to resolve source file relative path.</p>
     *
     * @return property.
     */
    @Input
    protected abstract Property<File> getBaseDirectory ();

    /**
     * Exec service.
     *
     * @return service
     */
    @Inject
    protected abstract ExecOperations getExec ();

    /**
     * File service.
     *
     * @return service
     */
    @Inject
    protected abstract FileOperations getFiles ();

    /**
     * Metal service.
     *
     * @return service
     */
    @ServiceReference
    protected abstract Property<MetalService> getMetal ();

    /**
     * Metal target.
     *
     * @return property
     */
    @Input
    public abstract Property<String> getTarget ();

    /**
     * Constructor.
     */
    protected MetalSourceTask ()
    {
        getBaseDirectory().convention( getProject().getProjectDir() );
        getTarget().convention( getMetal().map(MetalService::getTarget) );
    }
}
