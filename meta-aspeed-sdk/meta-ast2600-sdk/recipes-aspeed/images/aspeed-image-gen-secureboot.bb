DESCRIPTION = "Generate all secureboot images for AST2600. \
It is used for testing. Users should not use these generated images for production."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${ASPEEDSDKBASE}/LICENSE;md5=a3740bd0a194cd6dcafdc482a200a56f"
PACKAGE_ARCH = "${MACHINE_ARCH}"

PR = "r0"

DEPENDS = " \
    aspeed-image-tools-native \
    socsec-native \
    aspeed-secure-config-native \
    u-boot-tools-native \
    dtc-native \
    virtual/kernel \
    virtual/bootloader \
    "

do_patch[noexec] = "1"
do_configure[noexec] = "1"
do_compile[noexec] = "1"
do_install[noexec] = "1"

inherit python3native deploy

ASPEED_CUSTOMIZE_GEN_SECURE_IMAGE_ENABLE ?= "0"
ASPEED_CUSTOMIZE_GEN_SECURE_IMAGE ?= "\
    rsa2048-sha256 \
    rsa2048-sha256-o1 \
    rsa2048-sha256-o2-pub \
    rsa3072-sha384 \
    rsa3072-sha384-o1 \
    rsa3072-sha384-o2-pub \
    rsa4096-sha512 \
    rsa4096-sha512-o1 \
    rsa4096-sha512-o2-pub \
    "

DISTROOVERRIDES .= ":flash-${FLASH_SIZE}"
KERNEL_FITIMAGE_NAME = "fitImage-${INITRAMFS_IMAGE}-${MACHINE}-${MACHINE}"
KERNEL_FITIMAGE_ITS_NAME = "fitImage-its-${INITRAMFS_IMAGE}-${MACHINE}-${MACHINE}"
UBOOT_FITIMAGE_NAME = "u-boot.bin"
UBOOT_FITIMAGE_ITS_NAME = "u-boot.its"
SPL_IMAGE_NAME = "u-boot-spl.bin"

install_unsigned_image() {
    install -d ${S}/${GEN_IMAGE_MODE}
    install -d ${S}/${GEN_IMAGE_MODE}/arch
    install -d ${S}/${GEN_IMAGE_MODE}/arch/arm
    install -d ${S}/${GEN_IMAGE_MODE}/arch/arm/boot
    install -d ${S}/${GEN_IMAGE_MODE}/arch/arm/boot/dts

    # u-boot unsigned image, dtb and its
    install -m 0644 ${DEPLOY_DIR_IMAGE}/${UBOOT_FITIMAGE_ITS_NAME} ${S}/${GEN_IMAGE_MODE}
    install -m 0644 ${STAGING_DIR_HOST}/sysroot-only/u-boot* ${S}/${GEN_IMAGE_MODE}

    # kernel unsigned image, dtb and its
    install -m 0644 ${DEPLOY_DIR_IMAGE}/${KERNEL_FITIMAGE_ITS_NAME} ${S}/${GEN_IMAGE_MODE}
    install -m 0644 ${DEPLOY_DIR_IMAGE}/fitImage-linux.bin-${MACHINE} ${S}/${GEN_IMAGE_MODE}
    install -m 0644 ${DEPLOY_DIR_IMAGE}/fitImage-linux.bin-${MACHINE} ${S}/${GEN_IMAGE_MODE}/linux.bin
    install -m 0644 ${DEPLOY_DIR_IMAGE}/${KERNEL_DEVICETREE} ${S}/${GEN_IMAGE_MODE}
    install -m 0644 ${DEPLOY_DIR_IMAGE}/${KERNEL_DEVICETREE} ${S}/${GEN_IMAGE_MODE}/arch/arm/boot/dts
}

make_otp_image() {
    otptool_config="$(dirname ${OTPTOOL_CONFIGS})/${OTPTOOL_JSON}"
    otptool_config_slug="$(basename ${otptool_config} .json)"
    otptool_config_outdir="${S}/${GEN_IMAGE_MODE}/${otptool_config_slug}"
    otptool_user_folder="$([ -n "${OTPTOOL_USER_DIR}" ] && echo --user_data_folder ${OTPTOOL_USER_DIR})"

    echo "otptool_config=${otptool_config}"
    echo "otptool_user_folder=${otptool_user_folder}"
    echo "otptool_key_dir=${OTPTOOL_KEY_DIR}"
    echo "otptool_extra_opts=${OTPTOOL_EXTRA_OPTS}"

    mkdir -p "${otptool_config_outdir}"
    otptool make_otp_image \
        --key_folder ${OTPTOOL_KEY_DIR} \
        --output_folder "${otptool_config_outdir}" \
        ${otptool_user_folder} \
        ${otptool_config} \
        ${OTPTOOL_EXTRA_OPTS}

    if [ $? -ne 0 ]; then
        bbfatal "Generated OTP image failed."
    fi

    otptool print "${otptool_config_outdir}"/otp-all.image

    if [ $? -ne 0 ]; then
        bbfatal "Printed OTP image failed."
    fi
}

socsec_sign_spl_and_verify() {
    socsec_sign_key_dir="$(dirname ${SOCSEC_SIGN_KEY})"
    socsec_sign_key="${socsec_sign_key_dir}/${ROT_SIGN_KEY_NAME}"
    signing_extra_default_opts="--stack_intersects_verification_region=false --rsa_key_order=big"
    signing_extra_rsa_aes_key_opts=""
    signing_extra_aes_key_opts=""
    signing_helper_args=""
    signing_extra_opts=""

    if [ -n "${SOCSEC_SIGN_HELPER}" ]; then
        signing_helper_args="--signing_helper ${SOCSEC_SIGN_HELPER}"
    fi

    if [ -n "${ROT_AES_KEY_NAME}" -a -n "${ROT_RSA_AES_KEY_NAME}" ]; then
        signing_extra_aes_key_opts="--aes_key ${socsec_sign_key_dir}/${ROT_AES_KEY_NAME}"
        signing_extra_rsa_aes_key_opts="--rsa_aes ${socsec_sign_key_dir}/${ROT_RSA_AES_KEY_NAME}"
    elif [ -n "${ROT_AES_KEY_NAME}" ]; then
        signing_extra_aes_key_opts="--key_in_otp --aes_key ${socsec_sign_key_dir}/${ROT_AES_KEY_NAME}"
    fi

    signing_extra_opts="${signing_extra_default_opts} ${signing_extra_aes_key_opts} ${signing_extra_rsa_aes_key_opts}"

    echo "rot_sign_algo=${ROT_SIGN_ALGO}"
    echo "socsec_sign_key=${socsec_sign_key}"
    echo "signing_helper_args=${signing_helper_args}"
    echo "signing_extra_opts=${signing_extra_opts}"

    socsec make_secure_bl1_image \
        --soc ${SOCSEC_SIGN_SOC}  \
        --algorithm ${ROT_SIGN_ALGO} \
        --rsa_sign_key ${socsec_sign_key} \
        --bl1_image ${S}/${GEN_IMAGE_MODE}/${SPL_IMAGE_NAME} \
        $signing_helper_args \
        $signing_extra_opts \
        --output ${S}/${GEN_IMAGE_MODE}/${SPL_IMAGE_NAME}.staged

    rm -f ${S}/${GEN_IMAGE_MODE}/${SPL_IMAGE_NAME}
    mv ${S}/${GEN_IMAGE_MODE}/${SPL_IMAGE_NAME}.staged ${S}/${GEN_IMAGE_MODE}/${SPL_IMAGE_NAME}

    # verify spl and otp
    echo "verify otp and spl"
    socsec verify \
        --sec_image ${S}/${GEN_IMAGE_MODE}/${SPL_IMAGE_NAME} \
        --otp_image ${S}/${GEN_IMAGE_MODE}/"$(basename ${OTPTOOL_JSON} .json)"/otp-all.image

    if [ $? -ne 0 ]; then
        bbfatal "Verified OTP image failed."
    fi
}

make_uboot_kernel_fitimage_and_sign() {
    cd ${S}/${GEN_IMAGE_MODE}

    # Assemble the kernel image
    uboot-mkimage -f ${KERNEL_FITIMAGE_ITS_NAME} ${KERNEL_FITIMAGE_NAME}
    # Sign the Kernel FIT image and add public key to U-boot dtb
    uboot-mkimage -F -k ${UBOOT_SIGN_KEYDIR} -K "u-boot.dtb" -r ${KERNEL_FITIMAGE_NAME}
    # Verify kernel fitImage
    uboot-fit_check_sign -f ${KERNEL_FITIMAGE_NAME}  -k u-boot.dtb
    if [ $? -ne 0 ]; then
        bbfatal "Verified kernel fitImage failed."
    fi

    # Assemble the uboot image
    uboot-mkimage -f ${UBOOT_FITIMAGE_ITS_NAME} ${UBOOT_FITIMAGE_NAME}
    # Sign the U-boot FIT image and add public key to SPL dtb
    uboot-mkimage -F -k ${SPL_SIGN_KEYDIR} -K "u-boot-spl.dtb" -r ${UBOOT_FITIMAGE_NAME}
    # Verify U-boot fitImage
    uboot-fit_check_sign -f ${UBOOT_FITIMAGE_NAME}  -k u-boot-spl.dtb
    if [ $? -ne 0 ]; then
        bbfatal "Verified uboot fitImage failed."
    fi

    # concat spl dtb
    cat u-boot-spl-nodtb.bin u-boot-spl.dtb > ${SPL_IMAGE_NAME}

    rm -rf ${S}/${GEN_IMAGE_MODE}/arch
    rm -f ${S}/${GEN_IMAGE_MODE}/linux.bin

    cd ${S}
}

make_recovery_image() {
    python3 ${STAGING_BINDIR_NATIVE}/gen_uart_booting_image.py ${S}/${GEN_IMAGE_MODE}/${SPL_IMAGE_NAME} ${S}/${GEN_IMAGE_MODE}/recovery_${SPL_IMAGE_NAME}
}

deploy_static_image_helper() {
    otptool_config_slug="$(basename ${OTPTOOL_JSON} .json)"

    install -d ${DEPLOYDIR}
    install -d ${DEPLOYDIR}/${GEN_IMAGE_MODE}

    install -m 0644 ${DEPLOY_DIR_IMAGE}/image-rofs ${DEPLOYDIR}/${GEN_IMAGE_MODE}
    install -m 0644 ${DEPLOY_DIR_IMAGE}/image-rwfs ${DEPLOYDIR}/${GEN_IMAGE_MODE}
    install -m 0644 ${S}/${GEN_IMAGE_MODE}/*.* ${DEPLOYDIR}/${GEN_IMAGE_MODE}
    install -m 0644 ${S}/${GEN_IMAGE_MODE}/fitImage* ${DEPLOYDIR}/${GEN_IMAGE_MODE}
    install -m 0644 ${S}/${GEN_IMAGE_MODE}/${otptool_config_slug}/otp-all.image ${DEPLOYDIR}/${GEN_IMAGE_MODE}/${otptool_config_slug}-otp-all.image
    install -m 0644 ${DEPLOYDIR}/${GEN_IMAGE_MODE}/${KERNEL_FITIMAGE_NAME} ${DEPLOYDIR}/${GEN_IMAGE_MODE}/image-kernel
}


def make_empty_image(img, size_kb):
    size = int(size_kb) * 1024
    with open(img, "wb+") as fp:
        fp.seek(0)
        fp.write(b'\xFF'*size)


def update_its_file(file_path, oldstr, newstr):
    with open(file_path, 'r') as fp:
        file_contents = fp.read()
    new_contents = file_contents.replace(oldstr, newstr)
    with open(file_path, 'w') as fp:
        fp.write(new_contents)


def append_image(inimg, outimg, start_kb, finish_kb):
    import subprocess
    imgsize = os.path.getsize(inimg)
    maxsize = (finish_kb - start_kb) * 1024
    bb.debug(1, 'Considering file size=' + str(imgsize) + ' name=' + inimg)
    bb.debug(1, 'Spanning start=' + str(start_kb) + 'K end=' + str(finish_kb) + 'K')
    bb.debug(1, 'Compare needed=' + str(imgsize) + ' available=' + str(maxsize) + ' margin=' + str(maxsize - imgsize))
    if imgsize > maxsize:
        bb.fatal("Image '%s' is too large!" % inimg)

    cmd = "dd bs=1k conv=notrunc seek=%d if=%s of=%s" % (start_kb, inimg, outimg)
    print(cmd)
    subprocess.check_call(cmd, shell=True)


def deploy_static_image(d):
    bb.build.exec_func("deploy_static_image_helper", d)
    gen_img = d.getVar('GEN_IMAGE_MODE', True)

    # image-bmc
    nor_img = os.path.join(d.getVar('DEPLOYDIR', True), gen_img, "image-bmc")
    make_empty_image(nor_img, d.getVar('FLASH_SIZE', True))

    uboot_offset = int(d.getVar('FLASH_UBOOT_OFFSET', True))
    uboot_spl_end_offset = uboot_offset + int(d.getVar('FLASH_UBOOT_SPL_SIZE', True))
    append_image(os.path.join(d.getVar('DEPLOYDIR', True), gen_img, "u-boot-spl.bin"),
                 nor_img,
                 uboot_offset,
                 uboot_spl_end_offset)

    uboot_offset = uboot_spl_end_offset
    append_image(os.path.join(d.getVar('DEPLOYDIR', True), gen_img, "u-boot.bin"),
                 nor_img,
                 uboot_offset,
                 int(d.getVar('FLASH_UBOOT_ENV_OFFSET', True)))

    append_image(os.path.join(d.getVar('DEPLOYDIR', True), gen_img, "image-kernel"),
                 nor_img,
                 int(d.getVar('FLASH_KERNEL_OFFSET', True)),
                 int(d.getVar('FLASH_ROFS_OFFSET', True)))

    append_image(os.path.join(d.getVar('DEPLOYDIR', True), gen_img, "image-rofs"),
                 nor_img,
                 int(d.getVar('FLASH_ROFS_OFFSET', True)),
                 int(d.getVar('FLASH_RWFS_OFFSET', True)))

    append_image(os.path.join(d.getVar('DEPLOYDIR', True), gen_img, "image-rwfs"),
                 nor_img,
                 int(d.getVar('FLASH_RWFS_OFFSET', True)),
                 int(d.getVar('FLASH_SIZE', True)))

    # image-u-boot
    uboot_img = os.path.join(d.getVar('DEPLOYDIR', True), gen_img, "image-u-boot")
    make_empty_image(uboot_img, d.getVar('FLASH_UBOOT_ENV_OFFSET', True))

    uboot_offset = int(d.getVar('FLASH_UBOOT_OFFSET', True))
    uboot_spl_end_offset = uboot_offset + int(d.getVar('FLASH_UBOOT_SPL_SIZE', True))
    append_image(os.path.join(d.getVar('DEPLOYDIR', True), gen_img, "u-boot-spl.bin"),
                 uboot_img,
                 uboot_offset,
                 uboot_spl_end_offset)

    uboot_offset = uboot_spl_end_offset
    append_image(os.path.join(d.getVar('DEPLOYDIR', True), gen_img, "u-boot.bin"),
                 uboot_img,
                 uboot_offset,
                 int(d.getVar('FLASH_UBOOT_ENV_OFFSET', True)))


def verify_uboot_kernel_image_status(d):
    spl_binary = d.getVar('SPL_BINARY', True)
    if not spl_binary:
        bb.fatal("Only support SPL")

    kernel_imagetype = d.getVar('KERNEL_IMAGETYPE', True)
    if "fitImage" not in kernel_imagetype:
        bb.fatal("Only support Kernel fit image")

    uboot_fitimage_enable = d.getVar('UBOOT_FITIMAGE_ENABLE', True)
    if uboot_fitimage_enable != "1":
        bb.fatal("Only support UBoot fit image")

    spl_sign_enable = d.getVar('SPL_SIGN_ENABLE', True)
    if spl_sign_enable != "1":
        bb.fatal("Only support SPL sign enable")

    uboot_sign_enable = d.getVar('UBOOT_SIGN_ENABLE', True)
    if uboot_sign_enable != "1":
        bb.fatal("Only support UBoot sign enable")

    socsec_sign_enable = d.getVar('SOCSEC_SIGN_ENABLE', True)
    if socsec_sign_enable != "1":
        bb.fatal("Only support SOCSEC sign enable")


python do_deploy() {
    secure_image_list = [
        {
            "mode": "rsa2048-sha256",
            "otptool_json": "evbA3_RSA2048_SHA256.json",
            "rot_sign_algo" : "RSA2048_SHA256",
            "rot_sign_key_name" : "test_oem_dss_private_key_2048_1.pem",
            "rot_aes_key_name" : "",
            "rot_rsa_aes_key_name" : "",
            "cot_uboot_algo": "sha256,rsa2048",
            "cot_kernel_algo": "sha256,rsa2048",
            "cot_spl_sign_key_name": "test_bl2_2048",
            "cot_uboot_sign_key_name": "test_bl3_2048"
        },
        {
            "mode": "rsa2048-sha256-o1",
            "otptool_json": "evbA3_RSA2048_SHA256_o1.json",
            "rot_sign_algo" : "AES_RSA2048_SHA256",
            "rot_sign_key_name" : "test_oem_dss_private_key_2048_1.pem",
            "rot_aes_key_name" : "test_aes_key.bin",
            "rot_rsa_aes_key_name" : "",
            "cot_uboot_algo": "sha256,rsa2048",
            "cot_kernel_algo": "sha256,rsa2048",
            "cot_spl_sign_key_name": "test_bl2_2048",
            "cot_uboot_sign_key_name": "test_bl3_2048"
        },
        {
            "mode": "rsa2048-sha256-o2-pub",
            "otptool_json": "evbA3_RSA2048_SHA256_o2_pub.json",
            "rot_sign_algo" : "AES_RSA2048_SHA256",
            "rot_sign_key_name" : "test_oem_dss_private_key_2048_1.pem",
            "rot_aes_key_name" : "test_aes_key.bin",
            "rot_rsa_aes_key_name" : "test_soc_private_key_2048.pem",
            "cot_uboot_algo": "sha256,rsa2048",
            "cot_kernel_algo": "sha256,rsa2048",
            "cot_spl_sign_key_name": "test_bl2_2048",
            "cot_uboot_sign_key_name": "test_bl3_2048"
        },
        {
            "mode": "rsa3072-sha384",
            "otptool_json": "evbA3_RSA3072_SHA384.json",
            "rot_sign_algo" : "RSA3072_SHA384",
            "rot_sign_key_name" : "test_oem_dss_private_key_3072_1.pem",
            "rot_aes_key_name" : "",
            "rot_rsa_aes_key_name" : "",
            "cot_uboot_algo": "sha384,rsa3072",
            "cot_kernel_algo": "sha384,rsa3072",
            "cot_spl_sign_key_name": "test_bl2_3072",
            "cot_uboot_sign_key_name": "test_bl3_3072"
        },
        {
            "mode": "rsa3072-sha384-o1",
            "otptool_json": "evbA3_RSA3072_SHA384_o1.json",
            "rot_sign_algo" : "AES_RSA3072_SHA384",
            "rot_sign_key_name" : "test_oem_dss_private_key_3072_1.pem",
            "rot_aes_key_name" : "test_aes_key.bin",
            "rot_rsa_aes_key_name" : "",
            "cot_uboot_algo": "sha384,rsa3072",
            "cot_kernel_algo": "sha384,rsa3072",
            "cot_spl_sign_key_name": "test_bl2_3072",
            "cot_uboot_sign_key_name": "test_bl3_3072"
        },
        {
            "mode": "rsa3072-sha384-o2-pub",
            "otptool_json": "evbA3_RSA3072_SHA384_o2_pub.json",
            "rot_sign_algo" : "AES_RSA3072_SHA384",
            "rot_sign_key_name" : "test_oem_dss_private_key_3072_1.pem",
            "rot_aes_key_name" : "test_aes_key.bin",
            "rot_rsa_aes_key_name" : "test_soc_private_key_3072.pem",
            "cot_uboot_algo": "sha384,rsa3072",
            "cot_kernel_algo": "sha384,rsa3072",
            "cot_spl_sign_key_name": "test_bl2_3072",
            "cot_uboot_sign_key_name": "test_bl3_3072"
        },
        {
            "mode": "rsa4096-sha512",
            "otptool_json": "evbA3_RSA4096_SHA512.json",
            "rot_sign_algo" : "RSA4096_SHA512",
            "rot_sign_key_name" : "test_oem_dss_private_key_4096_1.pem",
            "rot_aes_key_name" : "",
            "rot_rsa_aes_key_name" : "",
            "cot_uboot_algo": "sha512,rsa4096",
            "cot_kernel_algo": "sha512,rsa4096",
            "cot_spl_sign_key_name": "test_bl2_4096",
            "cot_uboot_sign_key_name": "test_bl3_4096"
        },
        {
            "mode": "rsa4096-sha512-o1",
            "otptool_json": "evbA3_RSA4096_SHA512_o1.json",
            "rot_sign_algo" : "AES_RSA4096_SHA512",
            "rot_sign_key_name" : "test_oem_dss_private_key_4096_1.pem",
            "rot_aes_key_name" : "test_aes_key.bin",
            "rot_rsa_aes_key_name" : "",
            "cot_uboot_algo": "sha512,rsa4096",
            "cot_kernel_algo": "sha512,rsa4096",
            "cot_spl_sign_key_name": "test_bl2_4096",
            "cot_uboot_sign_key_name": "test_bl3_4096"
        },
        {
            "mode": "rsa4096-sha512-o2-pub",
            "otptool_json": "evbA3_RSA4096_SHA512_o2_pub.json",
            "rot_sign_algo" : "AES_RSA4096_SHA512",
            "rot_sign_key_name" : "test_oem_dss_private_key_4096_1.pem",
            "rot_aes_key_name" : "test_aes_key.bin",
            "rot_rsa_aes_key_name" : "test_soc_private_key_4096.pem",
            "cot_uboot_algo": "sha512,rsa4096",
            "cot_kernel_algo": "sha512,rsa4096",
            "cot_spl_sign_key_name": "test_bl2_4096",
            "cot_uboot_sign_key_name": "test_bl3_4096"
        }
    ]


    verify_uboot_kernel_image_status(d)
    gen_secure_image_enable = d.getVar('ASPEED_CUSTOMIZE_GEN_SECURE_IMAGE_ENABLE', True)
    if gen_secure_image_enable != "1":
        print("Disable gen secure image. Do nothing.")
        return

    uboot_default_algo = d.getVar('UBOOT_FIT_HASH_ALG', True) + "," + d.getVar('UBOOT_FIT_SIGN_ALG', True)
    kernel_default_algo = d.getVar('FIT_HASH_ALG', True) + "," + d.getVar('FIT_SIGN_ALG', True)
    spl_default_sign_key_name = d.getVar('SPL_SIGN_KEYNAME', True)
    uboot_default_sign_key_name = d.getVar('UBOOT_SIGN_KEYNAME', True)
    gen_secure_image = d.getVar('ASPEED_CUSTOMIZE_GEN_SECURE_IMAGE', True)

    for gen_img in gen_secure_image.split():
        for sec_img in secure_image_list:
            if gen_img == sec_img["mode"]:
                break
        else:
          bb.fatal("%s mode not support" % gen_img)

        print("Start %s image..." % gen_img)
        d.setVar('GEN_IMAGE_MODE', gen_img)
        d.setVar('OTPTOOL_JSON', sec_img["otptool_json"])
        d.setVar('ROT_SIGN_ALGO', sec_img["rot_sign_algo"])
        d.setVar('ROT_SIGN_KEY_NAME', sec_img["rot_sign_key_name"])
        d.setVar('ROT_AES_KEY_NAME', sec_img["rot_aes_key_name"])
        d.setVar('ROT_RSA_AES_KEY_NAME', sec_img["rot_rsa_aes_key_name"])

        bb.build.exec_func("install_unsigned_image", d)
        kernel_its = os.path.join(d.getVar('S', True), gen_img, d.getVar('KERNEL_FITIMAGE_ITS_NAME', True))
        print("Update kernel its file", kernel_its)
        update_its_file(kernel_its, kernel_default_algo, sec_img["cot_kernel_algo"])
        update_its_file(kernel_its, uboot_default_sign_key_name, sec_img["cot_uboot_sign_key_name"])
        uboot_its = os.path.join(d.getVar('S', True), gen_img, d.getVar('UBOOT_FITIMAGE_ITS_NAME', True))
        print("Update uboot its file", uboot_its)
        update_its_file(uboot_its, uboot_default_algo, sec_img["cot_uboot_algo"])
        update_its_file(uboot_its, spl_default_sign_key_name, sec_img["cot_spl_sign_key_name"])

        print("Make uboot kernel fitimage and sign")
        bb.build.exec_func("make_uboot_kernel_fitimage_and_sign", d)
        print("Make otp image")
        bb.build.exec_func("make_otp_image", d)
        print("SOCSEC sign spl and verify")
        bb.build.exec_func("socsec_sign_spl_and_verify", d)
        print("Make recovery image")
        bb.build.exec_func("make_recovery_image", d)
        print("Deploy static image")
        deploy_static_image(d)
        print("Started %s image" % gen_img)
}

do_deploy[depends] += " \
    obmc-phosphor-image:do_image_complete \
    "

addtask deploy before do_build after do_compile

python do_cleanall:prepend() {
    import subprocess
    gen_secure_image = [
        "rsa2048-sha256",
        "rsa2048-sha256-o1",
        "rsa2048-sha256-o2-pub",
        "rsa3072-sha384",
        "rsa3072-sha384-o1",
        "rsa3072-sha384-o2-pub",
        "rsa4096-sha512",
        "rsa4096-sha512-o1",
        "rsa4096-sha512-o2-pub"
    ]

    for gen_img in gen_secure_image:
        path = os.path.join(d.getVar('DEPLOY_DIR_IMAGE', True), gen_img)
        if os.path.exists(path):
            cmd = "rm -rf %s" % (path)
            print(cmd)
            subprocess.check_call(cmd, shell=True)
}

