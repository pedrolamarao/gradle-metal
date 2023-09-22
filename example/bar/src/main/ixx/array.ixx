export module br.dev.pedrolamarao.bar:array;

using _size = decltype(sizeof(nullptr));

export namespace br::dev::pedrolamarao::bar
{
    template <typename T, _size S>
    class array
    {
        T storage [S];

    public:

        array () = default;

        array (array const &) = default;

        array (array &&) = default;

        ~array () = default;

        auto operator= (array const &) -> array & = default;

        auto operator= (array &&) -> array & = default;

        void swap (array & that)
        {
            auto tmp { storage };
            storage = that.storage;
            that.storage = tmp;
        }

        //

        auto size () const
        {
            return S;
        }

        //

        auto get (_size index) const
        {
            return storage[index];
        }

        void put (_size index, T value)
        {
            storage[index] = value;
        }

    };
}