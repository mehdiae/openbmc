#!/bin/sh

SetupEndpoint()
{
	busctl call xyz.openbmc_project.MCTP \
	/xyz/openbmc_project/mctp au.com.CodeConstruct.MCTP SetupEndpoint \
	say "mctpi3c0" 6 0x07 0xec 0xa0 0x03 0x00 0x00

	busctl call xyz.openbmc_project.MCTP \
	/xyz/openbmc_project/mctp au.com.CodeConstruct.MCTP SetupEndpoint \
	say "mctpi3c1" 6 0x07 0xec 0xa0 0x03 0x20 0x00
}

GetPlatformState()
{
	# update PlatformState property
	busctl get-property xyz.openbmc_project.PFR.Manager /xyz/openbmc_project/pfr xyz.openbmc_project.State.Boot.Platform Data > /dev/null
	busctl get-property xyz.openbmc_project.PFR.Manager /xyz/openbmc_project/pfr xyz.openbmc_project.State.Boot.Platform PlatformState|cut -b 4-|awk -F '"' '{print $1}'
}

if [ -f /tmp/.mctp_i3c_done ];then
	exit 0
fi

if [ -r /dev/i3c-mctp-target-0 ];then

/usr/bin/pfr-mctpd -d /dev/i3c-mctp-target-0

else

STATE=$(GetPlatformState)
while true;do
if [ "$STATE" = "T0 BMC booted" ] || [ "$STATE" = "T0 boot complete" ];then
	SetupEndpoint
	break
fi
sleep 2
STATE=$(GetPlatformState)
done

fi
systemctl start i3c-attestation-emu.service
touch /tmp/.mctp_i3c_done
