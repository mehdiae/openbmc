SUMMARY = "Intel PFR Package Group"

PR = "r1"

PACKAGE_ARCH = "${TUNE_PKGARCH}"

inherit packagegroup

PROVIDES = "${PACKAGES}"
RPROVIDES:${PN} = "${PACKAGES}"

PACKAGES = " \
    ${PN}-apps \
    "

SUMMARY:${PN}-apps = "Intel PFR App package"
RDEPENDS:${PN}-apps = " \
    obmc-pfr-image \
    "

RDEPENDS:${PN}-apps:append:ast2600-dcscm = " \
    bmc-boot-done \
    "

RDEPENDS:${PN}-apps:append:ast2700-dcscm = " \
    bmc-boot-done \
    "
