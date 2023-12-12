MACHINE_FEATURES:append = " ast-mmc"
MACHINEOVERRIDES .= ":ast-mmc"

# This IMAGE_OVERHEAD_FACTOR allows for a percentage overage of the actual
# image size rather than a fixed extra space, this is space needed for
# initial startup and basic operations.
# IMAGE_OVERHEAD_FACTOR is 1.3 by default in bitbake.conf.
# And the IMAGE_ROOTFS_SIZE is IMAGE_ROOTFS * 1.3 by default.
# The rootfs is a readonly filesystem in openbmc for emmc boot.
# Set IMAGE_OVERHEAD_FACTOR 1 to reduce the code size.
IMAGE_OVERHEAD_FACTOR = "1.0"
