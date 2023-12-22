FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
SRC_URI:append = " file://ipmb-channels.json"
do_install:append() {
    install -m 0644 -D ${WORKDIR}/ipmb-channels.json \
                   ${D}/usr/share/ipmbbridge
}

# Disable ipmbbridged service by default
DISABLE_IPMBBRIDGED_SERVICE ?= "1"
SYSTEMD_SERVICE:${PN}:remove = "${@bb.utils.contains('DISABLE_IPMBBRIDGED_SERVICE', '1', 'ipmb.service', '', d)}"
FILES:${PN} += "${systemd_unitdir}"

