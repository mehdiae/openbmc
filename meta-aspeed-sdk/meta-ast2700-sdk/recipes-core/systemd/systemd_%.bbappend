FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

PACKAGECONFIG:remove = "\
    networkd \
    resolved \
    timesyncd \
    "
