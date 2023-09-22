export module br.dev.pedrolamarao.foo;

import br.dev.pedrolamarao.bar;

export namespace br::dev::pedrolamarao::foo
{
    using br::dev::pedrolamarao::bar::i32;

    constexpr i32 value { 123 };

    i32 foo (char const * arg);
}