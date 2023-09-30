#include <cstdio>

#include <greet.h>

int greet (int argc, char * argv[])
{
    return argc > 1 ? std::printf("Hello, %s\n",argv[1]) :  std::printf("Hello, World!\n");
}