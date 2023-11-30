#include <iostream>

import cpuid;

int main (int argc, char * argv[])
{
    auto [eax, ebx, ecx, edx] = cpuid(0);
    std::cout << eax << std::endl;
    return eax > 0 ? 0 : 1;
}