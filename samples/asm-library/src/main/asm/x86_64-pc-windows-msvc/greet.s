.intel_syntax

.global greet
greet:
        sub     rsp, 40
        lea     rcx, [rip + .L.str]
        call    printf
        nop
        add     rsp, 40
        ret
.L.str:
        .asciz  "Hello, World!\n"