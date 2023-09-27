// Copyright (C) 2022,2023 Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

#include <multiboot2/information.h>
#include <multiboot2/start.h>

namespace
{
    void debugcon (char const * message);
}

namespace multiboot2
{
    [[gnu::used]]
    void main ( size4 magic, multiboot2::information_list & response )
    {
        debugcon("gradle-metal");
    }
}

namespace
{
    void debugcon (char letter)
    {
        __asm__ __volatile__ ("outb %1, %0" : :  "dN" (0xE9), "a" (letter));
    }

    void debugcon (char const * message)
    {
        for (auto i = message; *i != '\0'; ++i) {
            debugcon(*i);
        }
    }
}