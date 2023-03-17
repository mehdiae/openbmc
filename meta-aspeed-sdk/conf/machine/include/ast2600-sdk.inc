SOC_FAMILY = "aspeed-g6"
include conf/machine/include/soc-family.inc
require conf/machine/include/aspeed-sdk.inc

SERIAL_CONSOLES ?= "115200;ttyS4"

DEFAULTTUNE = "armv7ahf-vfpv4d16"
UBOOT_ENTRYPOINT ?= "0x80000000"
UBOOT_LOADADDRESS ?= "0x80000000"

include conf/machine/include/arm/arch-armv7a.inc
