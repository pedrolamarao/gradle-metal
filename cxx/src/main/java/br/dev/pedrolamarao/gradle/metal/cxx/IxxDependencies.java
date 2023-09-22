package br.dev.pedrolamarao.gradle.metal.cxx;

import java.io.File;
import java.io.Serializable;
import java.util.List;

public record IxxDependencies (File file, List<String> provides, List<String> requires) implements Serializable
{
}
