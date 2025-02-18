package com.tap.unict.iotleaf.models.Modules;

import java.io.Serializable;
import java.util.Objects;

import com.tap.unict.iotleaf.models.SensorType;
import com.tap.unict.iotleaf.models.Device.Device;

public class ModuleConfigurationId implements Serializable {
    private Device device;
    private Long slot;
    private SensorType sensor;

    // Costruttore di default
    public ModuleConfigurationId() {
    }

    public ModuleConfigurationId(Device device, Long slot, SensorType sensor) {
        this.device = device;
        this.slot = slot;
        this.sensor = sensor;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    public Long getSlot() {
        return slot;
    }

    public void setSlot(Long slot) {
        this.slot = slot;
    }

    public SensorType getSensor() {
        return sensor;
    }

    public void setSensor(SensorType sensor) {
        this.sensor = sensor;
    }
    
    // equals() e hashCode()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModuleConfigurationId that = (ModuleConfigurationId) o;
        return Objects.equals(device, that.device) &&
               Objects.equals(slot, that.slot) &&
               Objects.equals(sensor, that.sensor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(device, slot, sensor);
    }
}