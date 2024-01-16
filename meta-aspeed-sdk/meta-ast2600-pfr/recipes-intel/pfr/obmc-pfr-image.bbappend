FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append = " file://pfr_manifest_ast2600_dcscm.json"

do_install:append() {
    install -d ${D}/${datadir}/pfrconfig
    install -m 400 ${WORKDIR}/pfr_manifest_ast2600_dcscm.json ${D}/${datadir}/pfrconfig
}
