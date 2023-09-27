package br.dev.pedrolamarao.gradle.metal.base;

import br.dev.pedrolamarao.gradle.metal.asm.MetalAsmSources;
import br.dev.pedrolamarao.gradle.metal.c.MetalCSources;
import br.dev.pedrolamarao.gradle.metal.cpp.MetalCppSources;
import br.dev.pedrolamarao.gradle.metal.cxx.MetalCxxSources;
import br.dev.pedrolamarao.gradle.metal.ixx.MetalIxxSources;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.Project;

abstract class MetalComponentPlugin
{
    protected void configure (Project project, MetalComponent component)
    {
        final var plugins = project.getPluginManager();

        project.getLogger().info("gradle-metal: creating main component: {}",component);

        plugins.withPlugin("br.dev.pedrolamarao.metal.cpp",plugin ->
        {
            final var metal = project.getExtensions().getByType(MetalExtension.class);
            final var cpp = (NamedDomainObjectContainer<?>) metal.getExtensions().getByName("cpp");
            final var sources = (MetalCppSources) cpp.create("main");
            project.getLogger().info("gradle-metal: creating main sources: {}",sources);
        });

        plugins.withPlugin("br.dev.pedrolamarao.metal.asm",plugin ->
        {
            final var metal = project.getExtensions().getByType(MetalExtension.class);
            final var asm = (NamedDomainObjectContainer<?>) metal.getExtensions().getByName("asm");
            final var sources = (MetalAsmSources) asm.create("main");
            component.source(sources.getOutputs());
            project.getLogger().info("gradle-metal: creating main sources: {}",sources);
        });

        plugins.withPlugin("br.dev.pedrolamarao.metal.c",plugin ->
        {
            final var metal = project.getExtensions().getByType(MetalExtension.class);
            final var c = (NamedDomainObjectContainer<?>) metal.getExtensions().getByName("c");
            final var sources = (MetalCSources) c.create("main");
            component.source(sources.getOutputs());
            project.getLogger().info("gradle-metal: creating main sources: {}",sources);
        });

        plugins.withPlugin("br.dev.pedrolamarao.metal.cxx",plugin ->
        {
            final var metal = project.getExtensions().getByType(MetalExtension.class);
            final var ixx = (NamedDomainObjectContainer<?>) metal.getExtensions().getByName("ixx");
            final var ixxSources = (MetalIxxSources) ixx.create("main");
            final var cxx = (NamedDomainObjectContainer<?>) metal.getExtensions().getByName("cxx");
            final var cxxSources = (MetalCxxSources) cxx.create("main");
            cxxSources.importable(ixxSources.getOutputDirectory());
            cxxSources.source(ixxSources.getOutputs());
            component.source(cxxSources.getOutputs());
            project.getLogger().info("gradle-metal: creating main sources: {}",cxxSources);
        });

        project.afterEvaluate(it ->
        {
            final var metal = it.getExtensions().getByType(MetalExtension.class);

            final var cppContainer = (NamedDomainObjectContainer<?>) metal.getExtensions().findByName("cpp");
            if (cppContainer == null) return;
            final var cpp = (MetalCppSources) cppContainer.getByName("main");

            final var asmContainer = (NamedDomainObjectContainer<?>) metal.getExtensions().findByName("asm");
            if (asmContainer != null) {
                final var asm = (MetalAsmSources) asmContainer.getByName("main");
                asm.includable( cpp.getSources().getSourceDirectories() );
                project.getLogger().info("gradle-metal: wiring sources: {} -> {}",cpp,asm);
            }

            final var cContainer = (NamedDomainObjectContainer<?>) metal.getExtensions().findByName("c");
            if (cContainer != null) {
                final var c = (MetalCSources) cContainer.getByName("main");
                c.includable( cpp.getSources().getSourceDirectories() );
                project.getLogger().info("gradle-metal: wiring sources: {} -> {}",cpp,c);
            }

            final var cxxContainer = (NamedDomainObjectContainer<?>) metal.getExtensions().findByName("cxx");
            if (cxxContainer != null) {
                final var cxx = (MetalCxxSources) cxxContainer.getByName("main");
                cxx.includable( cpp.getSources().getSourceDirectories() );
                project.getLogger().info("gradle-metal: wiring sources: {} -> {}",cpp,cxx);
            }
        });
    }
}
