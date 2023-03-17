GCCVERSION = "11.%"

SOC_FAMILY = "aspeed-g7"
include conf/machine/include/soc-family.inc
require conf/machine/include/aspeed-sdk.inc

DEFAULTTUNE = "cortexa35"

require conf/machine/include/arm/armv8a/tune-cortexa35.inc
require conf/machine/include/ast-arm-trustzone.inc

SERIAL_CONSOLES ?= "115200;ttyS12"

UBOOT_FIT_ADDRESS_CELLS ?= "2"
FIT_ADDRESS_CELLS ?= "2"

# Kernel fitimage
UBOOT_ENTRYPOINT ?= "0x4 0x00800000"
UBOOT_LOADADDRESS ?= "0x4 0x00800000"

# Uboot fitimage
ARM_TRUST_ZONE = "1"

ARM_TRUSTED_FIRMWARE_LOADADDRESS ?= "0x4 0x00000000"
ARM_TRUSTED_FIRMWARE_ENTRYPOINT ?= "0x4 0x00000000"
OPTEE_OS_LOADADDRESS ?= "0x4 0x00400000"
OPTEE_OS_ENTRYPOINT ?= "0x4 0x00400000"
UBOOT_FIT_UBOOT_ENTRYPOINT ?= "0x4 0x00840000"
UBOOT_FIT_UBOOT_LOADADDRESS ?= "0x4 0x00840000"

COMPATIBLE_MACHINE:aspeed-g7 = "aspeed-g7"
TFA_PLATFORM = "ast2700"

# optee for BL32.
TFA_SPD = "opteed"

# TFA default build is BL31.
TFA_BUILD_TARGET = "bl31"

# It is a shortcut for "make PLATFORM=aspeed PLATFORM_FLAVOR=ast2700"
OPTEEMACHINE = "aspeed-ast2700"

ARM_TRUSTED_FIRMWARE_PATH = "${DEPLOY_DIR_IMAGE}/${TFA_BUILD_TARGET}.bin"
OPTEE_PATH = "${DEPLOY_DIR_IMAGE}/optee/tee-raw.bin"
