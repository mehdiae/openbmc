SUMMARY = "measures the performance of cpu and mcu"
DESCRIPTION = "CoreMarkR is an industry-standard benchmark \
that measures the performance of central processing units \
(CPU) and embedded microcrontrollers (MCU)"

HOMEPAGE = "https://github.com/eembc/coremark"
SECTION = "benchmark"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=0a18b17ae63deaa8a595035f668aebe1"

SRCREV = "d5fad6bd094899101a4e5fd53af7298160ced6ab"

SRC_URI = "git://github.com/eembc/coremark.git;branch=main;protocol=https"

S = "${WORKDIR}/git"

do_compile() {
    oe_runmake PORT_DIR=linux "LFLAGS_END=${LDFLAGS}" link
}

do_install() {
    install -d ${D}/${bindir}
    install ${S}/coremark.exe ${D}/${bindir}
}

