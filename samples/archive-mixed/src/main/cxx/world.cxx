#include <cstdio>

#include <greet.h>

extern "C" char const * get_world ();

int world (int argc, char * argv [])
{
    return printf("%s",get_world());
}