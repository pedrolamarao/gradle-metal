#include <gtest/gtest.h>

TEST(foo,bar)
{
    ASSERT_FALSE(true);
}

int main (int argc, char * argv [])
{
  testing::InitGoogleTest(&argc, argv);
  return RUN_ALL_TESTS();
}
