import br.dev.pedrolamarao.bar;
import br.dev.pedrolamarao.foo;

extern "C" void meh ();

int main (int argc, char * argv [])
{
    using br::dev::pedrolamarao::bar::bar;
    using br::dev::pedrolamarao::foo::foo;

    return bar(argv[0]) + foo(argv[1]);
}