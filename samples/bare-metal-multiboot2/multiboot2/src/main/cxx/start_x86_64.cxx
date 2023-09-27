// Copyright (C) 2022,2023 Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

#include <multiboot2/start.h>

namespace multiboot2
{
    [[gnu::naked, gnu::section(".multiboot2.start")]]
    void start_x86_64 ()
    {
        //! TODO: activate x86 long mode.
        // Call main.
        __asm__
        {
            .code64
            push rbx
            push rax
            call main
        }
        // Halt.
        __asm__
        {
            .code64
        loop:
            hlt
            jmp loop
        }
    }
}