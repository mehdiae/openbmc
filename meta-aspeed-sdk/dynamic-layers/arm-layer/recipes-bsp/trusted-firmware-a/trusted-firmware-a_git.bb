require recipes-bsp/trusted-firmware-a/trusted-firmware-a.inc

# ASPEED upstream and it should fetch since 2.10.0
# TF-A
SRCREV_tfa = "2503c8f3204c60013de8caa2e165b2875ad735e5"
LIC_FILES_CHKSUM += "file://docs/license.rst;md5=b2c740efedc159745b9b31f88ff03dde"

# mbed TLS v2.28.2
SRC_URI_MBEDTLS = "git://github.com/ARMmbed/mbedtls.git;name=mbedtls;protocol=https;destsuffix=git/mbedtls;branch=mbedtls-2.28"
SRCREV_mbedtls = "89f040a5c938985c5f30728baed21e49d0846a53"

LIC_FILES_CHKSUM_MBEDTLS = "file://mbedtls/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"
