LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/GPL-2.0-or-later;md5=fed54355545ffd980b814dab4a3b312c"

inherit pkgconfig meson

SRC_URI = " git://gerrit.aspeed.com:29418/aspeed_app.git;protocol=ssh;branch=${BRANCH} "
SRC_URI:append:ast2600-dcscm = " file://0001-mctp-i3c-Add-thread-to-receive-mctp-response.patch "
SRC_URI:append:ast2700-dcscm = " file://0001-mctp-i3c-Add-thread-to-receive-mctp-response.patch "

PV = "1.0+git"
SRCREV = "${AUTOREV}"
BRANCH = "develop"

S = "${WORKDIR}/git"

DEPENDS += "openssl"
RDEPENDS:${PN} += "openssl"

FILES:${PN}:append = " /usr/share/* "
