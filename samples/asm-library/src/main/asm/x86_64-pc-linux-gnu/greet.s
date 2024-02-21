.intel_syntax

.global greet
greet:
        push    rbp
        mov     rbp, rsp
        lea     rdi, [rip + .L.str]
        mov     al, 0
        call    printf
        pop     rbp
        ret
.L.str:
        .asciz  "Hello, World!\n"