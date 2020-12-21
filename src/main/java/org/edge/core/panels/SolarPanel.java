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

    public SolarPanel(double efficiency, double area, Battery battery){
        this.efficiency = efficiency;
        this.area = area;
        this.battery = battery;
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
    public void supplyEnergy(double irradiance, double temperature, double angle){
        double powerPerDevice = getCurrentPowerOutput(irradiance, temperature, angle) / (suppliedDevices.size() + 1);

        for(EdgeDevice d : suppliedDevices){
//            TODO: some kind of a supplyPower function
            d.supplyPower(powerPerDevice);
        }

        battery.supplyPower(powerPerDevice);
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
