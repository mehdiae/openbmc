FILESEXTRAPATHS:prepend := "${THISDIR}/files:"

SRC_URI:append:ast2600-dcscm = " file://0001-change-boot-addr-dcscm.patch "
SRC_URI:append:ast2600-dcscm-amd = " file://0001-change-boot-addr-dcscm-amd.patch "
SRC_URI:append:ast2600-dcscm-avenue-city = " \
	file://0001-change-boot-addr-dcscm.patch \
	file://intel-avenue-city-crb.cfg \
"

