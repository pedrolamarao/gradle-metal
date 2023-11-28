.intel_syntax

.global _cpuid
// _cpuid(unsigned int, unsigned int*, unsigned int*, unsigned int*, unsigned int*):                      # @cpuid(unsigned int, unsigned int*, unsigned int*, unsigned int*, unsigned int*)
_cpuid:
        push    rbx
        mov     r10, rdx
        mov     eax, ecx
        mov     r11, qword ptr [rsp + 48]
        cpuid
        mov     dword ptr [r10], eax
        mov     dword ptr [r8], ebx
        mov     dword ptr [r9], ecx
        mov     dword ptr [r11], edx
        pop     rbx
        ret