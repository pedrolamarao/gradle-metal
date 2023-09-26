#include <cstdio>

#include <greet.h>

int greet (int argc, char * argv[])
{
   return argc == 1 ? std::printf("Hello, World!") : std::printf("Hello, %s!",argv[1]);
}
