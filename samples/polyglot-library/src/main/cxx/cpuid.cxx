module cpuid;

extern "C" void _cpuid (unsigned int request, unsigned int * eax, unsigned int * ebx, unsigned int * ecx, unsigned int * edx);

auto cpuid (unsigned int request) -> cpuid_type
{
    unsigned int eax, ebx, ecx, edx;
    _cpuid(request, &eax, &ebx, &ecx, &edx);
    return cpuid_type { eax, ebx, ecx, edx };
}