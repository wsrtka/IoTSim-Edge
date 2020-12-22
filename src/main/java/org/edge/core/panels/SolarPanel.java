package org.edge.core.panels;

import com.sun.javafx.geom.Edge;
import org.edge.core.edge.EdgeDevice;
import org.edge.core.feature.Battery;

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

    public SolarPanel(double efficiency, double area, Battery battery, double transportSpeed){
        this.efficiency = efficiency;
        this.area = area;
        this.battery = battery;
        this.transportSpeed = transportSpeed;
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
     */
    public void supplyEnergy(double irradiance, double temperature, double angle, int seconds){
        double powerPerDevice = getCurrentPowerOutput(irradiance, temperature, angle) / (suppliedDevices.size() + 1) * seconds / 1000;

        for(EdgeDevice d : suppliedDevices){
            if(powerPerDevice > 0) {
                d.supplyPower(powerPerDevice);
                System.out.println("Supplying device ID " + d.getId() + " with " + powerPerDevice + " energy from sun. Current battery capacity: " +
                        d.getCurrentBatteryCapacity());
            } else if(battery.getCurrentCapacity() > 0){
                d.supplyPower(transportSpeed);
                battery.setCurrentCapacity(battery.getCurrentCapacity() - transportSpeed);
                System.out.println("Supplying device ID " + d.getId() + " with " + transportSpeed + " energy from solar battery. Current device " +
                        "battery capacity: " + d.getCurrentBatteryCapacity() + ". Current solar battery capacity: " + battery.getCurrentCapacity());
            }

        }

        if(powerPerDevice > 0) {
            battery.setCurrentCapacity(battery.getCurrentCapacity() + powerPerDevice);
            System.out.println("Supplying solar battery with " + powerPerDevice + " energy from sun. Current battery capacity: " + battery.getCurrentCapacity());
        }

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
