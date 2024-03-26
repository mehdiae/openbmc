PFR_IMAGE_MODE = "${@bb.utils.contains('MACHINE_FEATURES', 'cerberus-pfr', 'cerberus-pfr-signing-image', 'intel-pfr-signing-image', d)}"
inherit ${PFR_IMAGE_MODE}

