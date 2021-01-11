package org.edge.core.feature.policy;
import org.edge.core.edge.EdgeDevice;
import org.edge.core.feature.Battery;

import java.util.LinkedList;
import java.util.List;

import static java.lang.Math.min;

/**
 * Power distribution strategy that prioritizes charging the device batteries. It will use battery power to charge device batteries if they are not charging at maximum speed. It will only charge the solar battery if each device is either fully charged or charging at the maximum rate.
 */
public class PowerDevicesFirst extends PowerDistributionVerboseStrategy {
    public PowerDevicesFirst(double maxSolarBatteryChargeRate, double maxDeviceBatteryChargeRate) {
        super(maxSolarBatteryChargeRate, maxDeviceBatteryChargeRate);
    }

    public void distributePower(double power, Battery solarBattery, double chargeTime, List<EdgeDevice> devices) {
        List<EdgeDevice> devicesToCharge = new LinkedList<>();
        devicesToCharge.addAll(devices);

        double leftoverPower = distributePowerToDevices(power, solarBattery, chargeTime, devicesToCharge, true);

        double solarBatteryCharge = min(min(leftoverPower, solarBattery.getMaxCapacity()-solarBattery.getCurrentCapacity()), maxSolarBatteryChargeRate*chargeTime); //battery charge limited by max charge rate and max capacity
        chargeSolarBattery(solarBatteryCharge, solarBattery);

        announceLeftoverPower(leftoverPower - solarBatteryCharge);
    }
}
