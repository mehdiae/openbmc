FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
SRC_URI:append:aspeed-g6 = " \
                 file://0001-change-pre-sensor-scaling.patch \
                 "
SRC_URI:append:aspeed-g7 = " \
                 file://0001-change-pre-sensor-scaling.patch \
                 "
