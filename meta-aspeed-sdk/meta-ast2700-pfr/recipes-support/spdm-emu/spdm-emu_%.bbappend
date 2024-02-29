FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " \
    file://mctp-init.sh \
    "

do_install:append() {
    install -m 0755 ${WORKDIR}/mctp-init.sh ${D}${bindir}
}
