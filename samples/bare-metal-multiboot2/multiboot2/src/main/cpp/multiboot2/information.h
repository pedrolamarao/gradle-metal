// Copyright (C) 2016,2023 Pedro Lamarão <pedro.lamarao@gmail.com>. All rights reserved.

#pragma once

#include <multiboot2/types.h>

//! Declarations

namespace multiboot2
{
  //! @brief Multiboot2 information magic number

  constexpr size4 information_magic = 0x36d76289;

  //! @brief Multiboot2 information list alignment

  constexpr unsigned information_alignment = 8;

  //! @brief Multiboot2 information list

  struct alignas(information_alignment) information_list
  {
    size4 size;
    size4 reserved;
  };

  //! @brief Multiboot2 information item type

  enum class information_type : size4
  {
    end              = 0,
    command_line     = 1,
    loader_name      = 2,
    modules          = 3,
    basic_memory     = 4,
    boot_device      = 5,
    memory_map       = 6,
    vbe              = 7,
    framebuffer      = 8,
    elf_symbols      = 9,
    apm              = 10,
    efi32            = 11,
    efi64            = 12,
    smbios           = 13,
    acpi_old         = 14,
    acpi_new         = 15,
    network          = 16,
  };

  namespace internal
  {
    struct information_item;
  }

  auto begin (information_list & list) -> internal::information_item * ;

  auto end (information_list & list) -> internal::information_item * ;

  auto next (internal::information_item * item) -> internal::information_item * ;

  //! @brief Multiboot2 end information list

  struct end_information
  {
    information_type type;
    size4    size;
  };

  //! @brief Multiboot2 command line information

  struct command_line_information
  {
    information_type type;
    size4    size;

    char command [];
  };

  //! @brief Multiboot2 loader name information

  struct loader_name_information
  {
    information_type type;
    size4    size;

    char name [];
  };

  //! @brief Multiboot2 modules information

  struct modules_information
  {
    information_type type;
    size4    size;

    size4 start;
    size4 end;
    char          command [];
  };

  //! @brief Multiboot2 basic memory information

  struct basic_memory_information
  {
    information_type type;
    size4    size;

    size4 lower;
    size4 upper;
  };

  //! @brief Multiboot2 boot device information

  struct boot_device_information
  {
    information_type type;
    size4    size;

    size4 device;
    size4 partition;
    size4 sub_partition;
  };

  //! @brief Multiboot2 memory map information

  struct memory_map_information
  {
    information_type type;
    size4    size;

    size4 entry_size;
    size4 entry_version;
  };

  //! @brief Multiboot2 ELF symbols information

  struct elf_symbols_information
  {
    information_type type;
    size4    size;

    size2 num;
    size2 entsize;
    size2 shndx;
    size2 reserved;
  };

  //! @brief Multiboot2 APM information

  struct apm_information
  {
    information_type type;
    size4    size;

    size2 version;
    size2 cseg;
    size2 offset;
    size2 cseg_16;
    size2 dseg;
    size2 flags;
    size2 cseg_len;
    size2 cseg_16_len;
    size2 dseg_len;
  };

  //! @brief Multiboot2 VBE information

  struct vbe_information
  {
    information_type type;
    size4    size;

    size2 mode;
    size2 interface_seg;
    size2 interface_off;
    size2 interface_len;
    size2 control_info;
    size2 mode_info;
  };

  //! @brief Multiboot2 framebuffer information

  struct framebuffer_information
  {
    information_type type;
    size4    size;

    size8 address;
    size4 pitch;
    size4 width;
    size4 height;
    size1  bpp;
    size4 framebuffer_type;
    size1  reserved;
  };

  //! @brief Multiboot2 EFI32 information

  struct efi32_information
  {
    information_type type;
    size4    size;

    size4 pointer;
  };

  //! @brief Multiboot2 EFI64 information

  struct efi64_information
  {
    information_type type;
    size4    size;

    size8 pointer;
  };

  //! @brief Multiboot2 SMBIOS information

  struct smbios_information
  {
    information_type type;
    size4    size;

    size1 major;
    size1 minor;
    size1 reserved [6];
    size1 tables   [];
  };

  //! @brief Multiboot2 ACPI information

  struct acpi_information
  {
    information_type type;
    size4    size;

    size1 rsdp [];
  };

  //! @brief Multiboot2 network information

  struct network_information
  {
    information_type type;
    size4    size;

    size1 dhcpack [];
  };

}

//! Inline definitions

namespace multiboot2
{

  namespace internal
  {
    struct information_item
    {
      information_type type;
      size4    size;
    };
  }

  inline
  auto begin (information_list & list) -> internal::information_item *
  {
    auto const base = reinterpret_cast<char *>(& list);
    auto const first = base + 8;
    return reinterpret_cast<internal::information_item *>(first);
  }

  inline
  auto end (information_list & list) -> internal::information_item *
  {
    auto const base = reinterpret_cast<char *>(& list);
    auto const last = base + list.size;
    return reinterpret_cast<internal::information_item *>(last);
  }

  inline
  auto next (internal::information_item * item) -> internal::information_item *
  {
    auto const base = reinterpret_cast<char *>(item);
    auto const successor = base + ((item->size + 7) & ~7);
    return reinterpret_cast<internal::information_item *>(successor);
  }

}
