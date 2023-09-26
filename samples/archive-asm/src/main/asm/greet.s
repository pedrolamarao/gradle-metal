.intel_syntax

.global greet
greet:
    pop rax
    call printf
    push 0
    ret