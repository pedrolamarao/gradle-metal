export module br.dev.pedrolamarao.bar:size;

using _size = decltype(sizeof(nullptr));

export namespace br::dev::pedrolamarao::bar
{
    template <_size Bits>
    class size
    {
        using storage_type = unsigned _BitInt(Bits);

        storage_type storage;

    public:

        size () = default;

        constexpr explicit size (storage_type value) : storage{value} {}

        size (size const &) = default;

        size (size &&) = default;

        ~size () = default;

        auto operator= (size const &) -> size & = default;

        auto operator= (size &&) -> size & = default;

        void swap (size & that)
        {
            auto tmp = storage;
            storage = that.storage;
            that.storage = tmp;
        }

        //

        template <typename T>
        static constexpr
        auto convert (T t) -> size
        {
            const storage_type x { t };
            return size{x};
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
        auto interpret (T t) -> size
        {
            const auto x { reinterpret_cast<storage_type>(t) };
            return size{x};
        }

        template <typename T>
        constexpr
        auto interpret () -> T
        {
            const auto x { reinterpret_cast<T>(storage) };
            return x;
        }

        //

        auto is_equal (size that) const
        {
            return storage == that.storage;
        }

        auto not_equal (size that) const
        {
            return storage != that.storage;
        }

        auto is_less (size that) const
        {
            return storage < that.storage;
        }

        auto is_greater (size that) const
        {
            return storage > that.storage;
        }

        auto is_not_greater (size that) const
        {
            return storage <= that.storage;
        }

        auto is_not_less (size that) const
        {
            return storage >= that.storage;
        }

        auto compare (size that) const
        {
            return storage <=> that.storage;
        }

        //

        auto sum (size that) const
        {
           return size{storage+that.storage};
        }

        auto difference (size that) const
        {
           return size{storage-that.storage};
        }

        auto product (size that) const
        {
           return size{storage*that.storage};
        }

        auto quotient (size that) const
        {
           return size{storage/that.storage};
        }

        auto remainder (size that) const
        {
           return size{storage%that.storage};
        }

        //

        auto doubled (_size times) const
        {
            return size{storage<<times};
        }

        auto halved (_size times) const
        {
            return size{storage>>times};
        }
    };

    //

    template <typename T, _size Bits>
    constexpr inline
    auto convert (size<Bits> value)
    {
        return value.convert<T>();
    }

    //

    template <_size Bits>
    constexpr inline
    auto sum (size<Bits> x, size<Bits> y)
    {
        return x.sum(y);
    }

    template <_size Bits>
    constexpr inline
    auto difference (size<Bits> x, size<Bits> y)
    {
        return x.difference(y);
    }

    template <_size Bits>
    constexpr inline
    auto product (size<Bits> x, size<Bits> y)
    {
        return x.product(y);
    }

    template <_size Bits>
    constexpr inline
    auto quotient (size<Bits> x, size<Bits> y)
    {
        return x.quotient(y);
    }

    template <_size Bits>
    constexpr inline
    auto remainder (size<Bits> x, size<Bits> y)
    {
        return x.remainder(y);
    }

    //

    template <_size Bits>
    constexpr inline
    auto doubled (size<Bits> x, _size times)
    {
        return x.doubled(times);
    }

    template <_size Bits>
    constexpr inline
    auto halved (size<Bits> x, _size times)
    {
        return x.halved(times);
    }

    //

    using s8  = size< 8>;
    using s16 = size<16>;
    using s32 = size<32>;
    using s64 = size<64>;
}