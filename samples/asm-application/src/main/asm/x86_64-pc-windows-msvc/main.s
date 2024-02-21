.intel_syntax

.global main
main:
        push    rbp
        sub     rsp, 64
        lea     rbp, [rsp + 64]
        lea     rcx, [rip + .L.str]
        call    printf
        xor     eax, eax
        add     rsp, 64
        pop     rbp
        ret
.L.str:
        .asciz  "Hello World!"