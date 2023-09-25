// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal.cxx;

import java.io.File;
import java.io.Serializable;
import java.util.List;

public record IxxModule (File source, List<String> provides, List<String> requires) implements Serializable
{
}
