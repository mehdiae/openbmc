# According to the design of AST2700, bootmcu(riscv-32) execute SPL and CPU(coretax-a35) execute u-boot.
# We added the do_merge_uboot task to merge the bootmcu and u-boot image before do_generate_static
# to ensure compatibility with image_types_phosphor.bbclass.
UBOOT_BINARY := "u-boot.${UBOOT_SUFFIX}"
UBOOT_SUFFIX:append = ".merged"

# Merge bootmcu and u-boot image
do_merge_uboot() {
    mk_empty_image_zeros ${DEPLOY_DIR_IMAGE}/u-boot.${UBOOT_SUFFIX} ${FLASH_BMCU_SIZE}

    # Check bootmcu size
    imgpath=${DEPLOY_DIR_IMAGE}/${BOOTMCU_FW_BINARY}
    imgsize=$(wc -c < "$imgpath")
    maxsize=$(expr ${FLASH_BMCU_SIZE} \* 1024)

    if [ "$imgsize" -gt "$maxsize" ]; then
        echo "Error: BOOTMCU image size ($imgsize bytes) exceeds $maxsize."
        exit 1
    fi

    # Concatenate bootmcu and u-boot image
    dd bs=1k seek=0 if=${DEPLOY_DIR_IMAGE}/${BOOTMCU_FW_BINARY} of=${DEPLOY_DIR_IMAGE}/u-boot.${UBOOT_SUFFIX}

    dd bs=1k seek=${FLASH_BMCU_SIZE} if=${DEPLOY_DIR_IMAGE}/${UBOOT_BINARY} of=${DEPLOY_DIR_IMAGE}/u-boot.${UBOOT_SUFFIX}
}

do_merge_uboot[depends] += " \
    u-boot:do_deploy \
    bootmcu-fw:do_deploy \
    "

addtask do_merge_uboot before do_generate_static after do_generate_rwfs_static

do_make_ubi[depends] += "${PN}:do_merge_uboot"
do_generate_ubi_tar[depends] += "${PN}:do_merge_uboot"
do_generate_static_tar[depends] += "${PN}:do_merge_uboot"
do_generate_static_norootfs[depends] += "${PN}:do_merge_uboot"
do_generate_ext4_tar[depends] += "${PN}:do_merge_uboot"
