package org.edge.core.feature.policy;

import org.edge.core.edge.EdgeDevice;
import org.edge.core.feature.Battery;

import java.util.List;

/**
 * A strategy for distributing the power produced by a solar panel between the innate battery and connected devices.
 *
 * Available strategies:
 * PowerBatteryFirst - focus on powering the solar battery in preparation for a ; only power devices if excess energy is produced; includes verbose logging
 * PowerDevicesFirst - prioritize charging device batteries; only charge the solar battery if excess energy is produced; includes verbose logging
 */
public interface PowerDistributionStrategy {
    /**
     * The main power distribution method. Distributes the generated solar energy between devices and the battery according to the selected strategy.
     *
     * @param power - the solar energy to distribute
     * @param solarBattery - the innate battery of the solar panel
     * @param chargeTime - charging time (in minutes)
     * @param devices - a list of devices to distribute power to
     */
    void distributePower(double power, Battery solarBattery, double chargeTime, List<EdgeDevice> devices);
}
