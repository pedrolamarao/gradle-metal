.intel_syntax

.global _cpuid
// _cpuid(unsigned int, unsigned int*, unsigned int*, unsigned int*, unsigned int*):                      # @cpuid(unsigned int, unsigned int*, unsigned int*, unsigned int*, unsigned int*)
_cpuid:
        push    rbx
        mov     r9, rcx
        mov     r10, rdx
        mov     eax, edi
        cpuid
        mov     dword ptr [rsi], eax
        mov     dword ptr [r10], ebx
        mov     dword ptr [r9], ecx
        mov     dword ptr [r8], edx
        pop     rbx
        ret