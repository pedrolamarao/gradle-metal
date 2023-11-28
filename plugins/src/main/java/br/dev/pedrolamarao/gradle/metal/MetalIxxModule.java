// Copyright (c) Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

package br.dev.pedrolamarao.gradle.metal;

import java.io.File;
import java.io.Serializable;
import java.util.List;

public record MetalIxxModule(File source, List<String> provides, List<String> requires) implements Serializable
{
}
