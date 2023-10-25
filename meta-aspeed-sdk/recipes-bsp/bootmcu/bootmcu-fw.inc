SUMMARY = "BootMCU firmware and execute SPL"
DESCRIPTION = "BootMCU is designated to load the first, verified image for the main processor"
HOMEPAGE = "https://github.com/AspeedTech-BMC/u-boot"
PACKAGE_ARCH = "${MACHINE_ARCH}"

DEPENDS = "ast2700-riscv-linux-gnu-native kern-tools-native swig-native"

S = "${WORKDIR}/git"
B = "${WORKDIR}/build"
do_configure[cleandirs] = "${B}"
do_install[noexec] = "1"

inherit deploy cml1

RISCV_PREBUILT_TOOLCHAIN = "${STAGING_DIR_NATIVE}${datadir}/ast2700-riscv-linux-gnu/bin/riscv32-unknown-linux-gnu-"

EXTRA_OEMAKE = 'CROSS_COMPILE=${RISCV_PREBUILT_TOOLCHAIN} CC="${RISCV_PREBUILT_TOOLCHAIN}gcc" V=1'
EXTRA_OEMAKE += 'HOSTCC="${BUILD_CC} ${BUILD_CFLAGS} ${BUILD_LDFLAGS}"'

PACKAGECONFIG ??= "openssl"
# u-boot will compile its own tools during the build, with specific
# configurations (aka when CONFIG_FIT_SIGNATURE is enabled) openssl is needed as
# a host build dependency.
PACKAGECONFIG[openssl] = ",,openssl-native"

# Allow setting an additional version string that will be picked up by the
# u-boot build system and appended to the u-boot version.  If the .scmversion
# file already exists it will not be overwritten.
UBOOT_LOCALVERSION ?= ""


# returns all the elements from the src uri that are .cfg files
def find_cfgs(d):
    sources=src_patches(d, True)
    sources_list=[]
    for s in sources:
        if s.endswith('.cfg'):
            sources_list.append(s)

    return sources_list


do_configure () {
    oe_runmake -C ${S} O=${B} ${BOOTMCU_MACHINE}
    merge_config.sh -m .config ${@" ".join(find_cfgs(d))}
    unset CFLAGS CPPFLAGS CXXFLAGS LDFLAGS
    yes '' | oe_runmake oldconfig
}

do_compile() {
    unset LDFLAGS
    unset CFLAGS
    unset CPPFLAGS

    if [ ! -e ${B}/.scmversion -a ! -e ${S}/.scmversion ]
    then
        echo ${UBOOT_LOCALVERSION} > ${B}/.scmversion
        echo ${UBOOT_LOCALVERSION} > ${S}/.scmversion
    fi

    oe_runmake -C ${S} O=${B} all
}

do_deploy() {
    install -d ${DEPLOYDIR}

    # spl
    install -m 644 ${B}/spl/u-boot-spl.dtb ${DEPLOYDIR}
    install -m 644 ${B}/spl/u-boot-spl-nodtb.bin ${DEPLOYDIR}
    install -m 644 ${B}/spl/u-boot-spl.bin ${DEPLOYDIR}

    # spl binman
    install -m 644 ${BOOTMCU_FW_BINARY} ${DEPLOYDIR}
}

addtask deploy before do_build after do_compile

python do_set_local_version() {
    import os
    s = d.getVar("S")
    b = d.getVar("B")
    try:
        os.remove('%s/.scmversion' % s)
        os.remove('%s/.scmversion' % b)
    except:
        pass

    scm_ver = ""

    try:
        res=bb.process.run("git -C %s describe --tags --exact-match" %  s)[0].strip("\n")

        if res.find("devtool") != -1:
            version_ext = bb.process.run("git -C %s rev-parse --verify --short HEAD" % s)[0].strip("\n")
            scm_ver = ('-%s-%s' % res, version_ext)
        else:
            scm_ver = ('-%s' % res)
    except Exception as e:
        version=bb.process.run("git -C %s rev-parse --verify --short HEAD" % s)[0].strip("\n")
        scm_ver = ('-dirty-%s' % version)

    with open("%s/.scmversion" % s, "a") as f:
        f.write(scm_ver)
    with open("%s/.scmversion" % b, "a") as f:
        f.write(scm_ver)
}

addtask set_local_version before do_compile after do_configure
