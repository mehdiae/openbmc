# There was an issue, "busctl introspect fails to show values for properties", in 252.4 version.
# issue: https://github.com/systemd/systemd/issues/26033
# Update to 252.5 which included this fiexd, "busctl: fix introspecting DBus properties".
# https://github.com/systemd/systemd-stable/commit/89e86ad8df4b87092264e49bcfba8053eb74822d
# This bbappend should be removed if rebase openbmc.

# https://github.com/systemd/systemd-stable/tree/v252.5
SRCREV = "61f5710d0bfd8f522af6f8eef399a851509946e2"
