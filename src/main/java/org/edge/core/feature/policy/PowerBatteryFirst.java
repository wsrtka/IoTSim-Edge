package org.edge.core.feature.policy;

import org.edge.core.edge.EdgeDevice;
import org.edge.core.feature.Battery;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static java.lang.Math.min;

public class PowerBatteryFirstStrategy extends PowerDistributionStrategy {
    public PowerBatteryFirstStrategy(double maxSolarBatteryChargeRate, double maxDeviceBatteryChargeRate, Battery solarBattery) {
        super(maxSolarBatteryChargeRate, maxDeviceBatteryChargeRate, solarBattery);
    }


    public void distributePower(double power, double chargeTime, List<EdgeDevice> devices) {
        double solarBatteryCharge = min(min(power, solarBattery.getMaxCapacity()-solarBattery.getCurrentCapacity()), maxSolarBatteryChargeRate*chargeTime); //battery charge limited by max charge rate and max capacity
        chargeSolarBattery(solarBatteryCharge);

        List<EdgeDevice> devicesToCharge = new LinkedList<>();
        devicesToCharge.addAll(devices);

        double devicesPower = power - solarBatteryCharge;
        double leftoverPower = distributePowerToDevices(devicesPower, chargeTime, devicesToCharge);

        announceLeftoverPower(leftoverPower);
    }
}
