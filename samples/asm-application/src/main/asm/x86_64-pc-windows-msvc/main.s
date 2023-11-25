.intel_syntax

.global main
main:
        sub     rsp, 40
        lea     rcx, [rip + .L.str]
        call    printf
        nop
        add     rsp, 40
        ret
.L.str:
        .asciz  "Hello World!"