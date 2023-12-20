FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"
SRC_URI:append:aspeed-g6 = " \
                 file://0001-change-pre-sensor-scaling-to-2.5v.patch \
                 file://0002-fansensor-support-ast2600-and-ast2700-pwm-driver.patch \
                 file://0003-fansensor-update-regular-expression-to-find-pwm.patch \
                 "
SRC_URI:append:aspeed-g7 = " \
                 file://0001-change-pre-sensor-scaling-to-2.5v.patch \
                 file://0002-fansensor-support-ast2600-and-ast2700-pwm-driver.patch \
                 file://0003-fansensor-update-regular-expression-to-find-pwm.patch \
                 "
# This patch is from https://gerrit.openbmc.org/c/openbmc/dbus-sensors/+/64039
# We can remove it after rebase OpenBMC.
SRC_URI:append = " \
    file://add-uring_args-for-all-build-targets.patch \
"

# Disable ipmbsensor service by default
DISABLE_IPMBSENSOR_SERVICE ?= "1"
SYSTEMD_SERVICE:${PN}:remove = "${@bb.utils.contains('DISABLE_IPMBSENSOR_SERVICE', '1', 'xyz.openbmc_project.ipmbsensor.service', '', d)}"
FILES:${PN} += "${systemd_unitdir}"