.intel_syntax

.global main
main:
        push    rbp
        mov     rbp, rsp
        sub     rsp, 16
        mov     dword ptr [rbp - 4], 0
        mov     dword ptr [rbp - 8], edi
        mov     qword ptr [rbp - 16], rsi
        call    greet
        xor     eax, eax
        add     rsp, 16
        pop     rbp
        ret