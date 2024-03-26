LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/GPL-2.0-or-later;md5=fed54355545ffd980b814dab4a3b312c"

inherit pkgconfig meson

SRC_URI = " git://github.com/AspeedTech-BMC/aspeed_app.git;protocol=https;branch=${BRANCH} "
SRC_URI:append:ast2600-dcscm = " file://0001-mctp-i3c-Add-thread-to-receive-mctp-response.patch "
SRC_URI:append:ast2700-dcscm = " file://0001-mctp-i3c-Add-thread-to-receive-mctp-response.patch "

PV = "1.0+git${SRCPV}"

# Tag for v00.01.12
SRCREV = "ce1c59bb7070f66879d44513848bb9d7ca665b77"
BRANCH = "master"

S = "${WORKDIR}/git"

DEPENDS += "openssl"
RDEPENDS:${PN} += "openssl"

FILES:${PN}:append = " /usr/share/* "
