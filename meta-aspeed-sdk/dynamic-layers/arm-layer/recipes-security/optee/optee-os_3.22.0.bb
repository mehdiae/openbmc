require recipes-security/optee/optee-os.inc

DEPENDS += "dtc-native"

SRCREV = "001ace6655dd6bb9cbe31aa31b4ba69746e1a1d9"

SRC_URI:append = " file://0001-arm-aspeed-Update-memory-layout.patch "