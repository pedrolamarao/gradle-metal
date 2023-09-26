package br.dev.pedrolamarao.gradle.metal.base;

import java.nio.file.Path;

public class MetalHash
{
    static final int OFFSET = 0x811c9dc5;

    static final int PRIME = 0x01000193;

    // see: https://en.wikipedia.org/wiki/Fowler–Noll–Vo_hash_function
    public static int hash (byte[] bytes)
    {
        int hash = OFFSET;
        for (byte b : bytes) {
            hash = hash * PRIME;
            hash = hash ^ b;
        }
        return hash;
    }

    public static int hash (String string)
    {
        return hash(string.getBytes());
    }

    public static int hash (Path path)
    {
        return hash(path.toString().getBytes());
    }
}
