IMAGE_INSTALL:remove:ast2700-fpga = " \
        packagegroup-oss-intel-pmci \
        packagegroup-aspeed-crypto \
        packagegroup-aspeed-ktools \
        packagegroup-aspeed-ssif \
        "

EXTRA_IMAGE_FEATURES:remove:ast2700-fpga = " \
        nfs-client \
        "