SUMMARY = "drgn (pronounced dragon) is a debugger with an emphasis on programmability."
HOMEPAGE = "https://github.com/osandov/drgn"
LICENSE = " LGPL-2.1-or-later"
LIC_FILES_CHKSUM = "file://COPYING;md5=7c83d30e99508d6b790eacdd3abab846"

SRC_URI = "git://github.com/osandov/drgn.git;protocol=https;branch=main"
SRCREV = "a8dfc9e31f551d4b3c3d4307fa19ab6b3bfafb1c"

S = "${WORKDIR}/git"

DEPENDS = "\
    autoconf-native \
    automake-native \
    libtool-native \
    elfutils \
    "

RDEPENDS:${PN} = "\
    libdw \
    libelf \
    libgomp \
    python3-crypt \
    python3-io \
    python3-logging \
    python3-math \
    python3-pickle \
    python3-stringold \
    "

export CONFIGURE_FLAGS = "\
    --build=${BUILD_SYS}, \
    --host=${HOST_SYS}, \
    --target=${TARGET_SYS}, \
    --prefix=${prefix}, \
    --exec_prefix=${exec_prefix}, \
    --bindir=${bindir}, \
    --sbindir=${sbindir}, \
    --libexecdir=${libexecdir}, \
    --datadir=${datadir}, \
    --sysconfdir=${sysconfdir}, \
    --sharedstatedir=${sharedstatedir}, \
    --localstatedir=${localstatedir}, \
    --libdir=${libdir}, \
    --includedir=${includedir}, \
    --oldincludedir=${includedir}, \
    --infodir=${infodir}, \
    --mandir=${mandir}, \
    --with-libtool-sysroot=${STAGING_DIR_HOST} \
    "

export PYTHON_CPPFLAGS = "-I${STAGING_INCDIR}/${PYTHON_DIR}"

inherit python3native pkgconfig setuptools3
