module;

#include <compare>

export module br.dev.pedrolamarao.bar:byte;

using _size = decltype(sizeof(nullptr));

export namespace be::dev::pedrolamarao::bar
{
    class byte
    {
        using storage_type = unsigned _BitInt(8);

        storage_type storage;

    public:

        byte () = default;

        constexpr explicit
        byte (storage_type value) : storage{value} {}

        byte (byte const &) = default;

        byte (byte &&) = default;

        ~byte () = default;

        auto operator= (byte const &) -> byte & = default;

        auto operator= (byte &&) -> byte & = default;

        void swap (byte & that)
        {
            auto tmp = storage;
            storage = that.storage;
            that.storage = tmp;
        }

        //

        constexpr
        auto is_equal (byte that) const
        {
            return storage == that.storage;
        }

        constexpr
        auto not_equal (byte that) const
        {
            return storage != that.storage;
        }

        constexpr
        auto is_less (byte that) const
        {
            return storage < that.storage;
        }

        constexpr
        auto is_greater (byte that) const
        {
            return storage > that.storage;
        }

        constexpr
        auto is_not_less (byte that) const
        {
            return storage <= that.storage;
        }

        constexpr
        auto is_not_greater (byte that) const
        {
            return storage >= that.storage;
        }

        constexpr
        auto compare (byte that) const
        {
            return storage <=> that.storage;
        }
    };
}