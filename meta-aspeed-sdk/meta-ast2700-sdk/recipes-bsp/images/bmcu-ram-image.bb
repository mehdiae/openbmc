SUMMARY = "bmcu ram prebuilt image"
DESCRIPTION = "bmcu ram prebuilt image"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/Apache-2.0;md5=89aea4e17d99a7cacdbeed46a0096b10"
PACKAGE_ARCH = "${MACHINE_ARCH}"

SRC_URI = " file://bmcu_ram.bin;subdir=${S}"

do_patch[noexec] = "1"
do_configure[noexec] = "1"
do_compile[noexec] = "1"
do_install[noexec] = "1"

inherit deploy

do_deploy () {
	install -D -m 644 ${S}/bmcu_ram.bin ${DEPLOYDIR}
}

addtask deploy before do_build after do_compile
