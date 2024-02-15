# AST2700 A0 doesn't support port80 direct to SGPIOS.
# We added a set-post-code-led to transfer BIOS post code values to SGPIOS[16:23].
IMAGE_INSTALL:append = " set-post-code-led"

