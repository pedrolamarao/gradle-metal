#include <stdio.h>

#include <greet.h>

extern char const * get_hello ();

int hello (int argc, char * argv[])
{
    return printf("%s",get_hello());
}