SOC_FAMILY = "aspeed-g5"
include conf/machine/include/soc-family.inc
require conf/machine/include/aspeed-sdk.inc

SERIAL_CONSOLES ?= "115200;ttyS4"

DEFAULTTUNE = "arm1176jzs"
UBOOT_ENTRYPOINT ?= "0x80000000"
UBOOT_LOADADDRESS ?= "0x80000000"

require conf/machine/include/arm/armv6/tune-arm1176jz-s.inc

