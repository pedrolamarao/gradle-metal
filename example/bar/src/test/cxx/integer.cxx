#include <gtest/gtest.h>

import br.dev.pedrolamarao.bar;

using namespace br::dev::pedrolamarao::bar;

TEST(integer,size)
{
    ASSERT_EQ( sizeof(uint8_t),  sizeof(i8) );
    ASSERT_EQ( sizeof(uint16_t), sizeof(i16) );
    ASSERT_EQ( sizeof(uint32_t), sizeof(i32) );
    ASSERT_EQ( sizeof(uint64_t), sizeof(i64) );
}
