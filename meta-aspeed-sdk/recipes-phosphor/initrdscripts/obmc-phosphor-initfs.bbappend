FILESEXTRAPATHS:prepend:spi-nor-ecc := "${THISDIR}/files:"

RDEPENDS:${PN}:append:spi-nor-ecc = " mtd-utils"

SRC_URI:append:spi-nor-ecc = " file://obmc-init.sh"

do_install:append:spi-nor-ecc() {
    install -m 0755 ${WORKDIR}/obmc-init.sh ${D}/init
}
