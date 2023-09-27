// Copyright (C) 2016,2023 Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

#pragma once

#include <multiboot2/types.h>

//! Declarations

namespace multiboot2
{
  //! @brief Multiboot2 header alignment

  constexpr unsigned header_alignment = 8;

  //! @brief Multiboot2 header tag alignment

  constexpr unsigned tag_alignment = 8;

  //! @brief Multiboot2 header magic number

  constexpr size4 header_magic = 0xe85250d6;

  //! @brief Multiboot2 request architecture

  enum class architecture_type : size4
  {
    x86    = 0,
    mips32 = 4,
  };


  //! @brief Multiboot2 header tag type

  enum class tag_type : size2
  {
    end         = 0,
    information = 1,
    address     = 2,
    entry       = 3,
    console     = 4,
    framebuffer = 5,
    module      = 6,
  };


  //! @brief Multiboot2 header prologue
  //!
  //! Multiboot2 headers have a prologue and a list of tags.

  struct alignas(header_alignment) header_prologue
  {
    size4              magic;
    architecture_type  architecture;
    size4              size;
    size4              checksum;

    constexpr
    header_prologue (architecture_type architecture, size4 size);
  };

  //! @brief Multiboot2 tag list end

  struct alignas(tag_alignment) end_request
  {
    tag_type  type  = tag_type::end;
    size2     flags = 0;
    size4     size  = 8;
  };

  //! @brief Multiboot2 request for loading a.out

  struct alignas(tag_alignment) address_request
  {
    tag_type  type;
    size2     flags;
    size4     size;

    size4     header_addr;
    size4     load_addr;
    size4     load_end_addr;
    size4     bss_end_addr;
  };

  //! @brief Multiboot2 request for loading ELF

  struct alignas(tag_alignment) entry_address_request
  {
    tag_type  type;
    size2     flags;
    size4     size;

    size4     entry_addr;
  };

  //! @brief Multiboot2 request for console support

  struct alignas(tag_alignment) console_request
  {
    tag_type  type;
    size2     flags;
    size4     size;

    size4     console_flags;

    constexpr
    console_request (size2 flags, size4 console_flags);

    constexpr explicit
    console_request (size4 console_flags);
  };

  //! @brief Multiboot2 request for framebuffer support

  struct alignas(tag_alignment) framebuffer_request
  {
    tag_type  type;
    size2     flags;
    size4     size;

    size4     width;
    size4     height;
    size4     depth;

    constexpr
    framebuffer_request (size2 flags, size4 width, size4 height, size4 depth);

    constexpr
    framebuffer_request (size4 width, size4 height, size4 depth);
  };

  //! @brief Multiboot2 request for module alignment

  struct alignas(tag_alignment) module_alignment_request
  {
    tag_type  type;
    size2     flags;
    size4     size;

    size4     unused [3];

    constexpr explicit
    module_alignment_request (size2 f);

    constexpr
    module_alignment_request ();
  };

}

//! Inline definitions

namespace multiboot2
{

  constexpr
  header_prologue::header_prologue (architecture_type a, size4 s) :
    magic(header_magic), architecture(a), size(s),
    checksum(- (header_magic + (size4)(architecture_type::x86) + s))
  { }

  constexpr
  console_request::console_request (size2 f, size4 cf) :
    type(tag_type::console), flags(f), size(sizeof(console_request)), console_flags(cf)
  { }

  constexpr
  console_request::console_request (size4 cf) :
    type(tag_type::console), flags(1), size(sizeof(console_request)), console_flags(cf)
  { }

  constexpr
  framebuffer_request::framebuffer_request (size2 f, size4 w, size4 h, size4 d) :
    type(tag_type::framebuffer), flags(f), size(sizeof(framebuffer_request)), width(w), height(h), depth(d)
  { }

  constexpr
  framebuffer_request::framebuffer_request (size4 w, size4 h, size4 d) :
    type(tag_type::framebuffer), flags(1), size(sizeof(framebuffer_request)), width(w), height(h), depth(d)
  { }

  constexpr inline
  module_alignment_request::module_alignment_request (size2 f) :
    type(tag_type::module), flags(f), size(sizeof(module_alignment_request)), unused{0,0,0}
  { }

  constexpr inline
  module_alignment_request::module_alignment_request () :
    type(tag_type::module), flags(1), size(sizeof(module_alignment_request)), unused{0,0,0}
  { }

}
