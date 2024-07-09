FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

# This is for AST2700 A0 workaround.
# We need to wait for x86-power-control to initialize some of the SGPIOs output pins, 
# then send the BMC boot complete event to AST1060 to start the SGPIO passthrough function.
SYSTEMD_OVERRIDE:${PN} += "power.conf:xyz.openbmc_project.PFR.Manager.service.d/power.conf"
