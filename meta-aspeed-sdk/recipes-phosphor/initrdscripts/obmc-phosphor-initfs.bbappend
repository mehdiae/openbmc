FILESEXTRAPATHS:prepend:spi-nor-ecc := "${THISDIR}/files:"
FILESEXTRAPATHS:prepend:ast2700-abr := "${THISDIR}/files:"

RDEPENDS:${PN}:append:spi-nor-ecc = " mtd-utils"

SRC_URI:append:spi-nor-ecc = " file://obmc-init.sh"

# Update obmc-init.sh and obmc-update.sh for AST2700 single flash ABR.
SRC_URI:append:ast2700-abr = " file://obmc-init-ast2700-ABR.sh \
                               file://obmc-update-ast2700-ABR.sh \
                             "

do_install:append:spi-nor-ecc() {
    install -m 0755 ${WORKDIR}/obmc-init.sh ${D}/init
}

do_install:append:ast2700-abr() {
    install -m 0755 ${WORKDIR}/obmc-init-ast2700-ABR.sh ${D}/init
    install -m 0755 ${WORKDIR}/obmc-update-ast2700-ABR.sh ${D}/update
}
