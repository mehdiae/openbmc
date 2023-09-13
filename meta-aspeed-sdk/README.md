# Create build environment
## Prerequisite

### Ubuntu 20.04
```
sudo apt install gawk wget git diffstat unzip texinfo gcc build-essential chrpath socat cpio python3 python3-pip python3-pexpect xz-utils \
    debianutils iputils-ping python3-git python3-jinja2 libegl1-mesa libsdl1.2-dev python3-subunit mesa-common-dev zstd liblz4-tool file locales
```

### Required Git, tar, Python, make, gcc/g++ Versions
- Git 1.8.3.1 or greater
- tar 1.28 or greater
- Python 3.8.0 or greater
- GNU make 4.0 or greater
- gcc/g++ 8.0 or greater

Reference:
- [OpenBMC/README.md](https://github.com/openbmc/openbmc#1-prerequisite)
- [Yocto Project Quick Build](https://docs.yoctoproject.org/brief-yoctoprojectqs/index.html)

## Target the machine
```
. setup <machine> [build_dir]
Target machine must be specified. Use one of:
ast2500-default
ast2500-default-515
ast2500-default-54
ast2600-a1
ast2600-a1-515
ast2600-a1-54
ast2600-a2
ast2600-a2-515
ast2600-a2-54
ast2600-a2-emmc
ast2600-dcscm
ast2600-dcscm-amd
ast2600-dcscm-avenue-city
ast2600-default
ast2600-default-515
ast2600-default-515-tee
ast2600-default-54
ast2600-default-54-tee
ast2600-default-tee
ast2600-ecc
ast2600-ecc-tee
ast2600-emmc
ast2600-emmc-secure-rsa2048-sha256
ast2600-emmc-secure-rsa2048-sha256-tee
ast2600-emmc-secure-rsa4096-sha512
ast2600-emmc-secure-rsa4096-sha512-tee
ast2600-emmc-tee
ast2600-ncsi
ast2600-ncsi-tee
ast2600-secure-rsa2048-sha256
ast2600-secure-rsa2048-sha256-ncot
ast2600-secure-rsa2048-sha256-ncot-tee
ast2600-secure-rsa2048-sha256-o1
ast2600-secure-rsa2048-sha256-o1-tee
ast2600-secure-rsa2048-sha256-o2-pub
ast2600-secure-rsa2048-sha256-o2-pub-tee
ast2600-secure-rsa2048-sha256-tee
ast2600-secure-rsa3072-sha384
ast2600-secure-rsa3072-sha384-o1
ast2600-secure-rsa3072-sha384-o1-tee
ast2600-secure-rsa3072-sha384-o2-pub
ast2600-secure-rsa3072-sha384-o2-pub-tee
ast2600-secure-rsa3072-sha384-tee
ast2600-secure-rsa4096-sha512
ast2600-secure-rsa4096-sha512-o1
ast2600-secure-rsa4096-sha512-o1-tee
ast2600-secure-rsa4096-sha512-o2-pub
ast2600-secure-rsa4096-sha512-o2-pub-tee
ast2600-secure-rsa4096-sha512-tee
ast2600-usbadp
ast2600-usbadp-tee
ast2700-default
ast2700-default-tee
ast2700-emmc
ast2700-emmc-tee
```

- The default kernel version is 6.1. machine with '-515' postfix for kernel v5.15 and machine with '54' postfix for kernel v5.04
- The optee-os is disabled by default. machine with 'tee' postfix for optee-os enable.
- The default revision for AST2600 is 'A3'. machine with 'a2' for AST2600 A2. machine with 'a1' for AST2600 A1. machine without 'a1 and a2' for AST2600 A3.

1. AST2700

```
. setup ast2700-default [build_dir]
```

2. AST2600

```
. setup ast2600-default [build_dir]
```

3. AST2500

```
. setup ast2500-default [build_dir]
```

## Build OpenBMC firmware

```
bitbake obmc-phosphor-image
```

# Output image
After you successfully built the image, the image file can be found in: `[build_dir]/tmp/work/deploy/images/${MACHINE}/`.

## OpenBMC firmware

### Boot from SPI image
- `image-bmc`: whole flash image
- `image-u-boot`: u-boot-spl.bin + u-boot.bin
- `image-kernel`: Linux Kernel FIT image
- `image-rofs`: read-only root file system

### Boot from SPI with secure boot image
- `image-bmc`: whole flash image
- `image-u-boot`: s_u-boot-spl.bin(RoT) + u-boot.bin (CoT1)
- `image-kernel`: Linux Kernel FIT Image the same as fitImage-${INITRAMFS_IMAGE}-${MACHINE}-${MACHINE} (CoT2)
- `image-rofs`: read-only root file system
- `s_u-boot-spl`: u-boot-spl.bin processed with socsec tool signing for RoT image
- `u-boot`: u-boot.bin processed with verified boot signing for CoT1 image
- `fitImage-${INITRAMFS_IMAGE}-${MACHINE}-${MACHINE}`: fitImage-${INITRAMFS_IMAGE}-${MACHINE}-${MACHINE} processed with verified boot signing for CoT2 image
- `otp_image`: OTP image

### Boot from eMMC image
- `emmc_image-u-boot`: u-boot-spl.bin + u-boot.bin processed with gen\_emmc\_image.py for boot partition
- `obmc-phosphor-image-${MACHINE}.wic.xz`: compressed emmc flash image for user data partition

### Boot from eMMC with secure boot image
- `s_emmc_image-u-boot`: s_u-boot-spl.bin(RoT) + u-boot.bin(CoT1) for boot partition
- `obmc-phosphor-image-${MACHINE}.wic.xz`: compressed emmc flash image for user data partition
- `s_u-boot_spl`: u-boot-spl.bin processed with socsec tool signing for RoT image
- `u-boot`: u-boot.bin processed with verified boot signing for CoT1 image
- `fitImage-${INITRAMFS_IMAGE}-${MACHINE}-${MACHINE}`: fitImage-${INITRAMFS_IMAGE}-${MACHINE}-${MACHINE} processed with verified boot signing for CoT2 image
- `otp_image`: OTP image

### Recovery Image via UART
- `recovery_u-boot-spl` : u-boot-spl.bin processed with gen_uart_booting_image.sh for recovery image via UART
- `recovery_s_u-boot-spl` : s_u-boot-spl.bin processed with gen_uart_booting_image.sh for recovery image via UART with secure boot

# Free Open Source Software (FOSS)
The Yocto/OpenBMC build system supports to provide the following things to meet the FOSS requirement.
- Source code must be provided.
- License text for the software must be provided.
- Compilation scripts and modifications to the source code must be provided.

The Yocto Project generates a license manifest during image creation that is located in ${DEPLOY_DIR}/licenses/image_name-datestamp to assist with any audits.
During the creation of your image, the source and patch from all recipes that deploy packages to the image is placed within subdirectories of DEPLOY_DIR/sources on the LICENSE for each recipe.
Please refer to [Working With Licenses](https://docs.yoctoproject.org/dev-manual/common-tasks.html#working-with-licenses) for detail.

To create it, please add the following settings in `local.conf`.
By default, it only creates for `GPL, LGPL and AGPL` LICENSE. User can add `COPYLEFT_LICENSE_INCLUDE = "*"` to create for all LICENSE.
Please refer to `archiver.bbclass` for detail.

```
INHERIT += "archiver"
ARCHIVER_MODE[src] = "original"
ARCHIVER_MODE[recipe] = "1"
COPYLEFT_LICENSE_INCLUDE = "*"
```

