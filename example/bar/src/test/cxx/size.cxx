#include <gtest/gtest.h>

import br.dev.pedrolamarao.bar;

using namespace br::dev::pedrolamarao::bar;

TEST(size,size)
{
    ASSERT_EQ( sizeof(uint8_t),   sizeof(s8) );
    ASSERT_EQ( sizeof(uint16_t),  sizeof(s16) );
    ASSERT_EQ( sizeof(uint32_t),  sizeof(s32) );
    ASSERT_EQ( sizeof(uint64_t),  sizeof(s64) );
    ASSERT_EQ( sizeof(uintptr_t), sizeof(size<>) );
}
