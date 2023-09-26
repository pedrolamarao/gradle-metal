#pragma once

#include <cstdio>

inline
int greet (int argc, char * argv[])
{
    return fprintf(stdout, "Hello World!\n");
}