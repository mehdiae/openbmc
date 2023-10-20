DESCRIPTION = "Generate recovery image via UART for ASPEED BMC SoCs"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://${ASPEEDSDKBASE}/LICENSE;md5=a3740bd0a194cd6dcafdc482a200a56f"
PACKAGE_ARCH = "${MACHINE_ARCH}"

PR = "r0"

DEPENDS = "aspeed-image-tools-native"

do_patch[noexec] = "1"
do_configure[noexec] = "1"
do_compile[noexec] = "1"
do_install[noexec] = "1"

inherit deploy

# Image composition
RECOVERY_INPUT_IMAGE ?= "u-boot-spl.bin"
RECOVERY_OUTPUT_IMAGE ?=  "recovery_${RECOVERY_INPUT_IMAGE}"

OUTPUT_IMAGE_DIR ?= "${S}/output"
SOURCE_IMAGE_DIR ?= "${S}/source"

do_deploy () {
    if [ -z ${SPL_BINARY} ]; then
        bbfatal "Boot from UART mode only support SPL"
    fi

    if [ -d ${SOURCE_IMAGE_DIR} ]; then
        rm -rf ${SOURCE_IMAGE_DIR}
    fi

    if [ -d ${OUTPUT_IMAGE_DIR} ]; then
        rm -rf ${OUTPUT_IMAGE_DIR}
    fi

    install -d ${SOURCE_IMAGE_DIR}
    install -d ${OUTPUT_IMAGE_DIR}

    # Install recovery input image into source directory
    # Generate recovery image via UART
    install -m 0644 ${DEPLOY_DIR_IMAGE}/${RECOVERY_INPUT_IMAGE} ${SOURCE_IMAGE_DIR}
    python3 ${STAGING_BINDIR_NATIVE}/gen_uart_booting_image.py ${SOURCE_IMAGE_DIR}/${RECOVERY_INPUT_IMAGE} ${OUTPUT_IMAGE_DIR}/${RECOVERY_OUTPUT_IMAGE}

    # Deploy recovery image via UART
    install -d ${DEPLOYDIR}
    install -m 644 ${OUTPUT_IMAGE_DIR}/${RECOVERY_OUTPUT_IMAGE} ${DEPLOYDIR}/.
}

do_deploy[depends] += " \
    virtual/kernel:do_deploy \
    virtual/bootloader:do_deploy \
    "

addtask deploy before do_build after do_compile
