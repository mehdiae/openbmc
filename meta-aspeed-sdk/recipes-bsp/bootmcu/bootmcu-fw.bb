SUMMARY = "BootMCU firmware"
DESCRIPTION = "BootMCU is designated to load the first, verified image for the main processor"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"
PACKAGE_ARCH = "${MACHINE_ARCH}"

BRANCH="aspeed-dev-ram"
SRC_URI = "git://gerrit.aspeed.com:29418/bootmcu.git;protocol=ssh;branch=${BRANCH}"
SRCREV = "${AUTOREV}"

S = "${WORKDIR}/git"

DEPENDS = "lowrisc-toolchain-gcc-rv32imcb-native"

AR[unexport] = "1"
AS[unexport] = "1"
CC[unexport] = "1"
CFLAGS[unexport] = "1"
CXX[unexport] = "1"
CXXFLAGS[unexport] = "1"
LD[unexport] = "1"
LDFLAGS[unexport] = "1"
OBJCOPY[unexport] = "1"
OBJDUMP[unexport] = "1"
STRIP[unexport] = "1"
NM[unexport] = "1"

do_install[noexec] = "1"

BOOTMCU_MACHINE ?= "ast2700-ram_defconfig"
BOOTMCU_FW_BINARY ?= "boot_mcu_ram.bin"

EXTRA_OEMAKE = " \
    CROSS_COMPILE=${STAGING_DIR_NATIVE}${datadir}/lowrisc-toolchain-gcc-rv32imcb/bin/riscv32-unknown-elf- \
    V=1 \
    "

inherit deploy

do_configure() {
    oe_runmake -C ${S} ${BOOTMCU_MACHINE}
}

do_compile() {
    oe_runmake -C ${S}
}

do_deploy() {
    install -D -m 644 ${S}/${BOOTMCU_FW_BINARY} ${DEPLOYDIR}
}

addtask deploy before do_build after do_compile
