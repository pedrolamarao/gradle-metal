#include <stdio.h>

#include "greet.h"

int greet (int argc, char * argv [])
{
    return argc == 1 ? printf("Hello, World!") : printf("Hello, %s!",argv[1]);
}
