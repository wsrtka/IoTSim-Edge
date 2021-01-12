package org.edge.core.feature.policy;

import org.edge.core.edge.EdgeDevice;
import org.edge.core.feature.Battery;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static java.lang.Math.min;

/**
 * Abstract strategy for distributing the power produced by a solar panel between the innate battery and connected devices.
 * Includes verbose logging methods and basic functionalities to be used in concrete implementations.
 */
public abstract class PowerDistributionVerboseStrategy implements PowerDistributionStrategy {
    protected double maxSolarBatteryChargeRate;
    protected double maxDeviceBatteryChargeRate;

    public PowerDistributionVerboseStrategy(double maxSolarBatteryChargeRate, double maxDeviceBatteryChargeRate){
        this.maxSolarBatteryChargeRate = maxSolarBatteryChargeRate;
        this.maxDeviceBatteryChargeRate = maxDeviceBatteryChargeRate;
    }

    /**
     * The main power distribution method. Distributes the generated solar energy between devices and the battery according to the selected strategy.
     *
     * @param power - the solar energy to distribute
     * @param chargeTime - charging time (in minutes)
     * @param devices - a list of devices to distribute power to
     */
    public abstract void distributePower(double power, Battery solarBattery, double chargeTime, List<EdgeDevice> devices);

    /**
     * Charges the solar battery by a certain amount of power and logs the result to console.
     *
     * @param power - added charge
     */
    protected void chargeSolarBattery(double power, Battery solarBattery){
        solarBattery.setCurrentCapacity(solarBattery.getCurrentCapacity()+power);
        System.out.println("Supplying solar battery with " + power + " energy from sun. Current solar battery capacity: " + solarBattery.getCurrentCapacity());
    }

    /**
     * Charges the device battery by a certain amount of power and logs the result to console. Logs include the power source - the sun.
     *
     * @param device - the device to charged
     * @param power - added charge
     */
    protected void chargeDeviceFromSun(EdgeDevice device, double power){
        device.supplyPower(power);
        System.out.println("Supplying device ID " + device.getId() + " with " + power + " energy from sun. Current device battery capacity: " +  device.getCurrentBatteryCapacity());
    }

    /**
     * Charges the device battery by a certain amount of power and logs the result to console. Logs include the power source - the solar battery.
     *
     * @param device - the device to charged
     * @param power - added charge
     */
    protected void chargeDeviceFromBattery(EdgeDevice device, Battery solarBattery, double power){
        if(solarBattery.getCurrentCapacity() - power >= 0) {
            device.supplyPower(power);
            solarBattery.setCurrentCapacity(solarBattery.getCurrentCapacity() - power);
        } else {
            device.supplyPower(solarBattery.getCurrentCapacity());
            power = solarBattery.getCurrentCapacity();
            solarBattery.setCurrentCapacity(0);
        }
        System.out.println("Supplying device ID " + device.getId() + " with " + power + " energy from the battery. Current solar battery capacity: " +  solarBattery.getCurrentCapacity() + ". Current device battery capacity: " +  device.getCurrentBatteryCapacity());
    }

    /**
     * Logs the leftover power that could not be used to charge anything.
     *
     * @param power - leftover energy
     */
    protected void announceLeftoverPower(double power){
        System.out.println(power+" excess solar energy was produced.");
    }

    /**
     * Distributes power evenly between a list of devices. Prevents overcharging and recursively redistributes excess power between other devices.
     *
     * @param power - energy to distribute
     * @param chargeTime - charging time (in minutes)
     * @param devices - a list of devices to distribute power to
     * @param fromBattery - if true, the solar battery can be used to fill up device batteries
     * @return leftover power after charging devices
     */
    protected double distributePowerToDevices(double power, Battery solarBattery, double chargeTime, List<EdgeDevice> devices, boolean fromBattery){
        List<EdgeDevice> devicesToCharge = new LinkedList<>();
        devicesToCharge.addAll(devices);

        double chargePerDevice = power/devices.size();
        double leftover = power;

        double deviceChargeLimit = chargeTime * this.maxDeviceBatteryChargeRate;

        Map<EdgeDevice, Double> deviceSolarCharges = new HashMap<>();
        Map<EdgeDevice, Double> deviceBatteryCharges = new HashMap<>();
        Map<EdgeDevice, Double> deviceChargeLimits = new HashMap<>();
        for(EdgeDevice ed: devicesToCharge){
            deviceSolarCharges.put(ed, 0.0);
            deviceBatteryCharges.put(ed, 0.0);
            deviceChargeLimits.put(ed, min(deviceChargeLimit, ed.getMaxBatteryCapacity()-ed.getCurrentBatteryCapacity()));
        }

        while (leftover > 0 && devicesToCharge.size() > 0){
            chargePerDevice = leftover/devicesToCharge.size();
            leftover = 0;
            for (EdgeDevice ed: devicesToCharge){
                double charge;
                if(chargePerDevice > deviceChargeLimits.get(ed)-deviceSolarCharges.get(ed)){
                    charge = deviceChargeLimits.get(ed)-deviceSolarCharges.get(ed);
                    devicesToCharge.remove(ed);
                    leftover += chargePerDevice - charge;
                }
                else charge = chargePerDevice;
                deviceSolarCharges.put(ed, charge + deviceSolarCharges.get(ed));
            }
        }

        if(fromBattery){
            for (EdgeDevice ed: devicesToCharge){
                deviceChargeLimits.put(ed, deviceChargeLimits.get(ed) - deviceSolarCharges.get(ed));
            }
            double batteryLeftover = solarBattery.getCurrentCapacity();

            while (batteryLeftover > 0 && devicesToCharge.size() > 0){
                chargePerDevice = batteryLeftover/devicesToCharge.size();
                batteryLeftover = 0;
                for (EdgeDevice ed: devicesToCharge){
                    double charge;
                    if(chargePerDevice > deviceChargeLimits.get(ed)-deviceBatteryCharges.get(ed)){
                        charge = deviceChargeLimits.get(ed)-deviceBatteryCharges.get(ed);
                        devicesToCharge.remove(ed);
                        batteryLeftover += chargePerDevice - charge;
                    }
                    else charge = chargePerDevice;
                    deviceBatteryCharges.put(ed, charge + deviceBatteryCharges.get(ed));
                }
            }
        }

        for(EdgeDevice ed: devices){
            chargeDeviceFromSun(ed, deviceSolarCharges.get(ed));
            if(fromBattery) chargeDeviceFromBattery(ed, solarBattery, deviceBatteryCharges.get(ed));
        }

        return leftover;
    }

}
