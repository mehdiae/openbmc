SUMMARY = "Two partition MTD image with u-boot and kernel"
HOMEPAGE = "https://github.com/openbmc/meta-aspeed"
LICENSE = "MIT"
PACKAGE_ARCH = "${MACHINE_ARCH}"

inherit ${@bb.utils.contains('MACHINE_FEATURES', 'ast-mmc', 'image', 'deploy', d)}

UBOOT_SUFFIX ?= "bin"

ASPEED_IMAGE_BMCU_RAM_IMAGE ?= "bmcu_ram"
ASPEED_IMAGE_UBOOT_SPL_IMAGE ?= "u-boot-spl"
ASPEED_IMAGE_UBOOT_IMAGE ?= "u-boot"

ASPEED_IMAGE_BMCU_RAM_OFFSET_KB ?= "0"
ASPEED_IMAGE_BMCU_RAM_SIZE_KB ?= "88"
ASPEED_IMAGE_UBOOT_SPL_OFFSET_KB ?= "384"
ASPEED_IMAGE_UBOOT_SPL_SIZE_KB ?= "128"
ASPEED_IMAGE_UBOOT_OFFSET_KB ?= "576"
ASPEED_IMAGE_UBOOT_SIZE_KB ?= "2048"
ASPEED_IMAGE_KERNEL_OFFSET_KB ?= "2624"

ASPEED_IMAGE_UBOOT_SPL_OFFSET_KB:aspeed-g6 ?= "0"
ASPEED_IMAGE_UBOOT_SPL_SIZE_KB:aspeed-g6 ?= "64"
ASPEED_IMAGE_UBOOT_OFFSET_KB:aspeed-g6 ?= "64"
ASPEED_IMAGE_UBOOT_SIZE_KB:aspeed-g6 ?= "832"
ASPEED_IMAGE_KERNEL_OFFSET_KB:aspeed-g6 ?= "1024"

ASPEED_IMAGE_UBOOT_OFFSET_KB:aspeed-g5 ?= "0"
ASPEED_IMAGE_UBOOT_SIZE_KB:aspeed-g5 ?= "960"
ASPEED_IMAGE_KERNEL_OFFSET_KB:aspeed-g5 ?= "1024"

ASPEED_IMAGE_SIZE_KB ?= "24576"
ASPEED_IMAGE_KERNEL_IMAGE ?= "fitImage-${INITRAMFS_IMAGE}-${MACHINE}-${MACHINE}"
ASPEED_IMAGE_NAME ?= "all.bin"
ASPEED_BOOT_EMMC ?= "${@bb.utils.contains('MACHINE_FEATURES', 'ast-mmc', 'yes', 'no', d)}"

IMAGE_FSTYPES:ast-mmc += "wic.xz mmc-ext4-tar"
IMAGE_FEATURES:ast-mmc += "read-only-rootfs-delayed-postinsts"

do_mk_empty_image() {
    # Assemble the flash image
    dd if=/dev/zero bs=1k count=${ASPEED_IMAGE_SIZE_KB} | \
        tr '\000' '\377' > ${B}/aspeed-sdk.bin
}

python do_compile() {
    import subprocess

    if d.getVar('ASPEED_BOOT_EMMC', True) == "yes":
        bb.fatal("MMC mode should not run this task")

    bb.build.exec_func("do_mk_empty_image", d)

    nor_image = os.path.join(d.getVar('B', True), "aspeed-sdk.bin")

    def _append_image(imgpath, start_kb, finish_kb):
        imgsize = os.path.getsize(imgpath)
        maxsize = (finish_kb - start_kb) * 1024
        bb.debug(1, 'Considering file size=' + str(imgsize) + ' name=' + imgpath)
        bb.debug(1, 'Spanning start=' + str(start_kb) + 'K end=' + str(finish_kb) + 'K')
        bb.debug(1, 'Compare needed=' + str(imgsize) + ' available=' + str(maxsize) + ' margin=' + str(maxsize - imgsize))
        if imgsize > maxsize:
            bb.fatal("Image '%s' is too large!" % imgpath)

        subprocess.check_call(['dd', 'bs=1k', 'conv=notrunc',
                                      'seek=%d' % start_kb,
                                      'if=%s' % imgpath,
                                      'of=%s' % nor_image])

    # bmcu ram
    bmcu_ram_binary = d.getVar('BMCU_RAM_BINARY', True)
    bmcu_ram_finish_kb = (int(d.getVar('ASPEED_IMAGE_BMCU_RAM_OFFSET_KB', True)) +
                         int(d.getVar('ASPEED_IMAGE_BMCU_RAM_SIZE_KB', True)))
    if bmcu_ram_binary:
        _append_image(os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True),
                                   '%s.%s' % (
                                   d.getVar('ASPEED_IMAGE_BMCU_RAM_IMAGE', True),
                                   d.getVar('UBOOT_SUFFIX', True))),
                      int(d.getVar('ASPEED_IMAGE_BMCU_RAM_OFFSET_KB', True)),
                      bmcu_ram_finish_kb)

    # spl
    spl_binary = d.getVar('SPL_BINARY', True)
    spl_finish_kb = (int(d.getVar('ASPEED_IMAGE_UBOOT_SPL_OFFSET_KB', True)) +
                    int(d.getVar('ASPEED_IMAGE_UBOOT_SPL_SIZE_KB', True)))
    if spl_binary:
        _append_image(os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True),
                                   '%s.%s' % (
                                   d.getVar('ASPEED_IMAGE_UBOOT_SPL_IMAGE', True),
                                   d.getVar('UBOOT_SUFFIX', True))),
                      int(d.getVar('ASPEED_IMAGE_UBOOT_SPL_OFFSET_KB', True)),
                      spl_finish_kb)

    # uboot fit
    uboot_finish_kb = (int(d.getVar('ASPEED_IMAGE_UBOOT_OFFSET_KB', True)) +
                      int(d.getVar('ASPEED_IMAGE_UBOOT_SIZE_KB', True)))
    _append_image(os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True),
                  '%s.%s' % (
                  d.getVar('ASPEED_IMAGE_UBOOT_IMAGE', True),
                  d.getVar('UBOOT_SUFFIX', True))),
                  int(d.getVar('ASPEED_IMAGE_UBOOT_OFFSET_KB', True)),
                  uboot_finish_kb)

    # kernel fit
    _append_image(os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True),
                  '%s' %
                  d.getVar('ASPEED_IMAGE_KERNEL_IMAGE',True)),
                  int(d.getVar('ASPEED_IMAGE_KERNEL_OFFSET_KB', True)),
                  int(d.getVar('ASPEED_IMAGE_SIZE_KB', True)))
}

python do_compile:ast-mmc() {
    bb.debug(1, "MMC mode do nothing")
}

do_deploy() {
    if test "x${ASPEED_BOOT_EMMC}" = "xyes"; then
        bbfatal "MMC mode should not run this task"
    fi

    install -m644 -D ${B}/aspeed-sdk.bin ${DEPLOYDIR}/${ASPEED_IMAGE_NAME}
}

do_deploy:ast-mmc() {
    :
}

do_compile[depends] = " \
    virtual/kernel:do_deploy \
    u-boot:do_deploy \
    ${@bb.utils.contains('MACHINE_FEATURES', 'ast-secure', 'aspeed-image-secureboot:do_deploy', '', d)} \
    "
do_fetch[noexec] = "1"
do_unpack[noexec] = "1"
do_patch[noexec] = "1"
do_configure[noexec] = "1"
do_install[noexec] = "1"
deltask do_populate_sysroot
do_package[noexec] = "1"
deltask do_package_qa
do_packagedata[noexec] = "1"
deltask do_package_write_ipk
deltask do_package_write_deb
deltask do_package_write_rpm
addtask deploy before do_build after do_compile
