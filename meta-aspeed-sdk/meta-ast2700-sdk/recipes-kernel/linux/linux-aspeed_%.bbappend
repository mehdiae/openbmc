FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI:append = " \
    file://default-hung-task-timeout-1200s.cfg \
"
