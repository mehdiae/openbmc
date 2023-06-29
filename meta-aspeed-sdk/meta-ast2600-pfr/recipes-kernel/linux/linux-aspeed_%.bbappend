FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append:ast2600-dcscm-avenue-city = " \
	file://mctp-i3c.cfg \
"

