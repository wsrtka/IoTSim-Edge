package org.edge.core.feature.policy;
import org.edge.core.edge.EdgeDevice;
import org.edge.core.feature.Battery;

import java.util.LinkedList;
import java.util.List;

import static java.lang.Math.min;

public class PowerDevicesFirstStrategy extends PowerDistributionStrategy {
    private double maxMainBatteryCharge;
    private List<EdgeDevice> devices;

    public PowerDevicesFirstStrategy(double maxSolarBatteryChargeRate, double maxDeviceBatteryChargeRate, Battery solarBattery) {
        super(maxSolarBatteryChargeRate, maxDeviceBatteryChargeRate, solarBattery);
    }

    public void distributePower(double power, double chargeTime, List<EdgeDevice> devices) {
        List<EdgeDevice> devicesToCharge = new LinkedList<>();
        devicesToCharge.addAll(devices);

        double leftoverPower = distributePowerToDevices(power, chargeTime, devicesToCharge);

        double solarBatteryCharge = min(min(leftoverPower, solarBattery.getMaxCapacity()-solarBattery.getCurrentCapacity()), maxSolarBatteryChargeRate*chargeTime); //battery charge limited by max charge rate and max capacity
        chargeSolarBattery(solarBatteryCharge);

        announceLeftoverPower(leftoverPower - solarBatteryCharge);
    }
}
