FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SYSTEMD_OVERRIDE:${PN} += "hotjoin.conf:BootCompleted.service.d/hotjoin.conf"
