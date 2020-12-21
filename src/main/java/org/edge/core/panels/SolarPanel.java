package org.edge.core.panels;

public class SolarPanel {

    private double efficiency;
    private double area;

    public SolarPanel(double efficiency, double area){
        this.efficiency = efficiency;
        this.area = area;
    }

    public double getCurrentPowerOutput(double irradiance, double temperature){
        return efficiency
                * area
                * irradiance
                * (1 - 0.005 * (temperature - 25));
    }

}
