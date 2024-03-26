FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append:ast2600-dcscm = " \
	file://mctp.cfg \
"

