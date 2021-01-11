package org.edge.core.panels;

import org.edge.core.edge.EdgeDevice;
import org.edge.core.feature.Battery;
import org.edge.core.feature.policy.PowerDevicesFirst;
import org.edge.core.feature.policy.PowerDistributionStrategy;

import java.util.LinkedList;
import java.util.List;

public class SolarPanel {

    /**
     * Efficiency of the solar panel (between 0 and 100%)
     */
    private double efficiency;

    /**
     * Area of the solar panel in m**2
     */
    private double area;

    /**
     * Devices supplied by this solar panel.
     */
    private List<EdgeDevice> suppliedDevices = new LinkedList<>();

    /**
     * Battery to load the surplus energy to.
     */
    private Battery battery;

    /**
     * Energy transport speed from solar battery to device battery.
     */

    private double transportSpeed;

    /**
     * Strategy for distributing the power between the innate battery and connected devices.
     * Currently available strategies include:
     * PowerBatteryFirst - focus on powering the solar battery; only power devices if excess energy is produced
     * PowerDevicesFirst - prioritize charging device batteries; only charge the solar battery if excess energy is produced
     */

    private PowerDistributionStrategy strategy;

    public SolarPanel(double efficiency, double area, Battery battery, double transportSpeed){
        this.efficiency = efficiency;
        this.area = area;
        this.battery = battery;
        this.transportSpeed = transportSpeed;

        double maxSolarBatteryChargeRate = this.transportSpeed*2;
        this.strategy = new PowerDevicesFirst(maxSolarBatteryChargeRate, this.transportSpeed);
    }

    /**
     * Change the strategy for distributing the power between the innate battery and connected devices
     * @param strategy - the chosen strategy
     */
    public void setPowerDistributionStrategy(PowerDistributionStrategy strategy){
        this.strategy = strategy;
    }

    /**
     * Add a device to the list of supplied devices
     * @param e is the device
     * @return true if device was added
     */
    public boolean connect(EdgeDevice e){
        return suppliedDevices.add(e);
    }

    /**
     * Removes a device to the list of supplied devices
     * @param e is the device
     * @return true if device was removed
     */
    public boolean disconnect(EdgeDevice e){
        return suppliedDevices.remove(e);
    }

    /**
     * Supply energy to devices and battery. Function is supposed to be used in a main loop.
     * @param irradiance - ready to use solar irradiance
     * @param temperature - temperature of solar panel, default should be 25
     * @param angle - angle between the panel surface and sun, default should be 90
     * @param seconds - simulation time in seconds
     */
    public void supplyEnergy(double irradiance, double temperature, double angle, int seconds){
        double power = getCurrentPowerOutput(irradiance, temperature, angle)  * seconds / 1000;
        strategy.distributePower(power, this.battery, seconds/60.0, this.suppliedDevices);
    }

    /**
     * Calculate current power output
     * @param irradiance - ready to use solar irradiance
     * @param temperature - temperature of solar panel, default should be 25
     * @param angle - angle between the panel surface and sun, default should be 90
     * @return power output in W
     */
    public double getCurrentPowerOutput(double irradiance, double temperature, double angle){
        return efficiency
                * area
                * (irradiance * Math.sin(angle))
                * (1 - 0.005 * (temperature - 25));
    }

}
