#pragma once

#include <stdio.h>

inline
int greet (int argc, char * argv[])
{
    return fprintf(stdout, "Hello World!\n");
}