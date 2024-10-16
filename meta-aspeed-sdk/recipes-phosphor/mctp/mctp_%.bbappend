FILESEXTRAPATHS:prepend := "${THISDIR}/files:"


do_install:append() {
   install -m 755 ${WORKDIR}/build/mctp-req ${D}${bindir}
   install -m 755 ${WORKDIR}/build/mctp-echo ${D}${bindir}
}
