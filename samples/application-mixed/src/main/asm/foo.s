.intel_syntax

// written in a rush, most certainly doesnt't work, please fix this
.global world
world:
    pop rax
    pop rax
    push 0
    call printf
    ret

.data
message: .ascii " World!"