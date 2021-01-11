package org.edge.core.feature.policy;

import org.edge.core.edge.EdgeDevice;
import org.edge.core.feature.Battery;

import java.util.List;

import static java.lang.Math.min;

/**
 * Power distribution strategy that prioritizes charging the solar battery. It will not use solar battery to power devices. Devices are only charged when battery is full or charging at the maximum speed.
 */
public class PowerBatteryFirst extends PowerDistributionVerboseStrategy {
    public PowerBatteryFirst(double maxSolarBatteryChargeRate, double maxDeviceBatteryChargeRate) {
        super(maxSolarBatteryChargeRate, maxDeviceBatteryChargeRate);
    }


    public void distributePower(double power, Battery solarBattery, double chargeTime, List<EdgeDevice> devices) {
        double solarBatteryCharge = min(min(power, solarBattery.getMaxCapacity()-solarBattery.getCurrentCapacity()), maxSolarBatteryChargeRate*chargeTime); //battery charge limited by max charge rate and max capacity
        chargeSolarBattery(solarBatteryCharge, solarBattery);

        double devicesPower = power - solarBatteryCharge;
        double leftoverPower = distributePowerToDevices(devicesPower, solarBattery, chargeTime, devices, false);

        announceLeftoverPower(leftoverPower);
    }
}
