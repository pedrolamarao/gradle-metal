module;

#include <cstdio>

module br.dev.pedrolamarao.greet;

int greet (int argc, char * argv[])
{
   return argc == 1 ? std::printf("Hello, World!") : std::printf("Hello, %s!",argv[1]);
}