export module br.dev.pedrolamarao.bar:integer;

using _size = decltype(sizeof(nullptr));

export namespace br::dev::pedrolamarao::bar
{
    template <_size Bits>
    class integer
    {
        using storage_type = signed _BitInt(Bits);

        storage_type storage;

    public:

        integer () = default;

        constexpr integer (storage_type value) : storage{value} {}

        integer (integer const &) = default;

        integer (integer &&) = default;

        ~integer () = default;

        auto operator= (integer const &) -> integer & = default;

        auto operator= (integer &&) -> integer & = default;

        void swap (integer & that)
        {
            auto tmp = storage;
            storage = that.storage;
            that.storage = tmp;
        }

        //

        template <typename T>
        static constexpr
        auto convert (T t) -> integer
        {
            const storage_type x { t };
            return integer{x};
        }

        template <typename T>
        constexpr
        auto convert () const -> T
        {
            const T x { storage };
            return x;
        }

        template <typename T>
        static constexpr
        auto interpret (T t) -> integer
        {
            const auto x { reinterpret_cast<storage_type>(t) };
            return integer{x};
        }

        template <typename T>
        constexpr
        auto interpret () -> T
        {
            const auto x { reinterpret_cast<T>(storage) };
            return x;
        }

        //

        auto is_equal (integer that) const
        {
            return storage == that.storage;
        }

        auto not_equal (integer that) const
        {
            return storage != that.storage;
        }

        auto is_less (integer that) const
        {
            return storage < that.storage;
        }

        auto is_greater (integer that) const
        {
            return storage > that.storage;
        }

        auto is_not_less (integer that) const
        {
            return storage <= that.storage;
        }

        auto is_not_greater (integer that) const
        {
            return storage >= that.storage;
        }

        auto compare (integer that) const
        {
            return storage <=> that.storage;
        }

        //

        auto sum (integer that) const
        {
           return integer{storage+that.storage};
        }

        auto difference (integer that) const
        {
           return integer{storage-that.storage};
        }

        auto product (integer that) const
        {
           return integer{storage*that.storage};
        }

        auto quotient (integer that) const
        {
           return integer{storage/that.storage};
        }

        auto remainder (integer that) const
        {
           return integer{storage%that.storage};
        }

        //

        auto doubled (_size times) const
        {
            return integer{storage<<times};
        }

        auto halved (_size times) const
        {
            return integer{storage>>times};
        }
    };

    //

    template <typename T, _size Bits>
    constexpr inline
    auto convert (integer<Bits> value)
    {
        return value.convert<T>();
    }

    //

    template <_size Bits>
    constexpr inline
    auto sum (integer<Bits> x, integer<Bits> y)
    {
        return x.sum(y);
    }

    template <_size Bits>
    constexpr inline
    auto difference (integer<Bits> x, integer<Bits> y)
    {
        return x.difference(y);
    }

    template <_size Bits>
    constexpr inline
    auto product (integer<Bits> x, integer<Bits> y)
    {
        return x.product(y);
    }

    template <_size Bits>
    constexpr inline
    auto quotient (integer<Bits> x, integer<Bits> y)
    {
        return x.quotient(y);
    }

    template <_size Bits>
    constexpr inline
    auto remainder (integer<Bits> x, integer<Bits> y)
    {
        return x.remainder(y);
    }

    //

    template <_size Bits>
    constexpr inline
    auto doubled (integer<Bits> x, _size times)
    {
        return x.doubled(times);
    }

    template <_size Bits>
    constexpr inline
    auto halved (integer<Bits> x, _size times)
    {
        return x.halved(times);
    }

    //

    using i8  = integer< 8>;
    using i16 = integer<16>;
    using i32 = integer<32>;
    using i64 = integer<64>;
}