require zephyr-kernel.inc
require zephyr-kernel-common.inc
inherit deploy

ZEPHYR_SRC_DIR = "${S}/samples/philosophers"
ZEPHYR_BASE = "${S}"

do_compile () {
    cd ${ZEPHYR_SRC_DIR}
    oe_runmake ${ZEPHYR_MAKE_ARGS}
}

do_deploy () {
    install -D samples/philosophers/outdir/${BOARD}/zephyr.elf ${DEPLOYDIR}/${PN}.elf
    install -D samples/philosophers/outdir/${BOARD}/zephyr.bin ${DEPLOYDIR}/${PN}.bin
}

addtask deploy after do_compile
