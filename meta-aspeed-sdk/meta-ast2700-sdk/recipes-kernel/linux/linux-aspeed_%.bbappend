FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

# By default, CONFIG_DEFAULT_HUNG_TASK_TIMEOUT is 120 seconds.
# However, AST2700 FPGA is very slow because its sys clk is 24MHZ.
# Set CONFIG_DEFAULT_HUNG_TASK_TIMEOUT 1200 seconds.

SRC_URI:append:ast2700-fpga = " \
	file://default-hung-task-timeout-1200s.cfg \
"

