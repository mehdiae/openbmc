KBRANCH = "aspeed-dev-v6.1"
LINUX_VERSION ?= "6.1.15"

SRCREV = "${AUTOREV}"
LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

require linux-aspeed.inc

DEPENDS += "lzop-native"
