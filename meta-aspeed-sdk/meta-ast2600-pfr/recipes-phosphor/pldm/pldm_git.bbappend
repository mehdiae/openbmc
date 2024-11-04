FILESEXTRAPATHS:prepend := "${THISDIR}/files:"
inherit obmc-phosphor-systemd


SRC_URI:append = " \
	file://host_eid"


do_install:append () {
	install -d ${D}${datadir}/pldm
	install -m 0644 ${WORKDIR}/host_eid ${D}${datadir}/pldm/
}