#!/bin/bash

# Define GPIO pins
GPIO_PINS=PWRGD_S0_PWROK_CPU0

interrupt_handler() {
    echo "Received SIGINT. Stopping the loop."
    exit 0
}

trap interrupt_handler SIGINT

gpio_pin=$(gpiofind "$GPIO_PINS")
if [ -z "$gpio_pin" ]; then
    echo "power-status-sync $GPIO_PINS not found"
    exit 1
fi

last_gpio_value=2

while true
do
    gpio_value=$(gpioget $gpio_pin)

    if [ "$gpio_value" != "$last_gpio_value" ]; then
        if [ "$gpio_value" -eq 0 ]
        then
            busctl set-property xyz.openbmc_project.Inventory.Manager \
                /xyz/openbmc_project/inventory/system/chassis/host0/PowerOk \
                xyz.openbmc_project.State.Decorator.OperationalStatus Functional b false
        else
            busctl set-property xyz.openbmc_project.Inventory.Manager \
                /xyz/openbmc_project/inventory/system/chassis/host0/PowerOk \
                xyz.openbmc_project.State.Decorator.OperationalStatus  Functional b true
        fi
    fi
    last_gpio_value=$gpio_value
    sleep 1
done
