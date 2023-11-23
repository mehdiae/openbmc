FILESEXTRAPATHS:append := "${THISDIR}/${PN}:"

SRC_URI:append = " file://create_usbhid.sh"
SRC_URI:append = " file://obmc-ikvm-ast2700.patch"


FILES:${PN}:append = " \
    ${bindir}/create_usbhid.sh \
"

do_install:append () {
    install -d ${D}${bindir} ${D}${systemd_system_unitdir}

    install -D -m 0755 ${WORKDIR}/create_usbhid.sh ${D}${bindir}
}
