FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

# By default, PLAT_SYSCNT_CLKIN_HZ is 1.6GHZ for coretexa35.
# However, ast2700 fpga PLAT_SYSCNT_CLKIN_HZ is 24MHZ.
# Update PLAT_SYSCNT_CLKIN_HZ for AST2700 FPGA.
SRC_URI:append:ast2700-fpga = " file://fpga_clk.patch "
