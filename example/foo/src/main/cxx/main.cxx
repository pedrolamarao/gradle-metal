#include <cstdio>

import br.dev.pedrolamarao.bar;
import br.dev.pedrolamarao.foo;

using namespace br::dev::pedrolamarao::bar;
using namespace br::dev::pedrolamarao::foo;

int main (int argc, char * argv [])
{
    array<s32,4> numbers {};

    std::printf("Hello World!\n");

    return convert<int>( sum( bar(argv[0]), foo(argv[1]) ) );
}