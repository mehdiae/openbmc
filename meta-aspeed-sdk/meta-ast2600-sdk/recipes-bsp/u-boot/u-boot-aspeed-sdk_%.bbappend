FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

inherit socsec-sign otptool

SRC_URI:append:ast-mmc = " \
    file://u-boot-env-ast2600.txt \
    "
