export module cpuid;

export struct cpuid_type { unsigned int eax, ebx, ecx, edx; };

export auto cpuid (unsigned int request) -> cpuid_type;