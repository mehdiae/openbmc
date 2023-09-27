# The following variables can be overridden in local.conf
# Enable aspeed customize secure boot image generation
ASPEED_CUSTOMIZE_GEN_SECURE_IMAGE_ENABLE ?= "1"

# Generate aspeed customize secure boot image
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
