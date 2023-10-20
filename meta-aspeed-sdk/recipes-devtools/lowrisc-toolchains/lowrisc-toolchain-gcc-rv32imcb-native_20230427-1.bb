SUMMARY = "lowRISC prebuit toolchain"
DESCRIPTION = "A GCC RV32IMC without hardfloat support, targeting Ibex"
LICENSE = "GPL-3.0-with-GCC-exception & GPL-3.0-only"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"

SRC_URI = "https://github.com/lowRISC/lowrisc-toolchains/releases/download/${PV}/${BPN}-${PV}.tar.xz"

SRC_URI[md5sum] = "d0d6e20a0ddfd065613dc52817bc3cdf"
SRC_URI[sha256sum] = "dc12404a9cba43670b60b2026de041ad06decbc4ffb0298f5311846f84928b58"

do_patch[noexec] = "1"
do_configure[noexec] = "1"
do_compile[noexec] = "1"

inherit native

do_install() {
    install -d -m 0755 ${D}${datadir}/lowrisc-toolchain-gcc-rv32imcb
    cp --no-preserve=ownership -rf ${S}/* ${D}${datadir}/lowrisc-toolchain-gcc-rv32imcb
}

INHIBIT_DEFAULT_DEPS = "1"

INSANE_SKIP:${PN} = "already-stripped libdir staticdev file-rdeps arch dev-so ldflags"

INHIBIT_SYSROOT_STRIP = "1"
INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
