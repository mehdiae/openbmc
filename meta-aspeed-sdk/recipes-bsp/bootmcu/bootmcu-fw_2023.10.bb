require bootmcu-fw.inc

LICENSE = "GPL-2.0-or-later"
LIC_FILES_CHKSUM = "file://Licenses/README;md5=2ca5f2c35c8cc335f0a19756634782f1"

DEPENDS += "flex-native bison-native bc-native dtc-native"

PV = "v2023.10+git${SRCPV}"

BRANCH="aspeed-dev-v2023.10"
SRC_URI = "git://gerrit.aspeed.com:29418/u-boot.git;protocol=ssh;branch=${BRANCH}"
SRCREV = "${AUTOREV}"

