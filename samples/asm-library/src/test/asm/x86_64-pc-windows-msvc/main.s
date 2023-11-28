.intel_syntax

.global main
main:
        mov     QWORD PTR [rsp+16], rdx
        mov     DWORD PTR [rsp+8], ecx
        sub     rsp, 40
        call    greet
        xor     eax, eax
        add     rsp, 40
        ret     0
