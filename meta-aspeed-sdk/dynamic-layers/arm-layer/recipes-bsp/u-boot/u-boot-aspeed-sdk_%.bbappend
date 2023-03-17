do_compile[depends] += " \
    ${@bb.utils.contains('MACHINE_FEATURES', 'ast-arm-trustzone', 'optee-os:do_deploy', '', d)} \
    ${@bb.utils.contains('MACHINE_FEATURES', 'ast-arm-trustzone', 'trusted-firmware-a:do_deploy', '', d)} \
    "
