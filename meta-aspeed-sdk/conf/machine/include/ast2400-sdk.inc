SOC_FAMILY = "aspeed-g4"
include conf/machine/include/soc-family.inc
require conf/machine/include/aspeed-sdk.inc

SERIAL_CONSOLES ?= "115200;ttyS4"

UBOOT_ENTRYPOINT ?= "0x40000000"
UBOOT_LOADADDRESS ?= "0x40000000"

require conf/machine/include/arm/armv5/tune-arm926ejs.inc
