package com.tap.unict.iotleaf.models.Modules;

import com.tap.unict.iotleaf.models.SensorType;
import com.tap.unict.iotleaf.models.Device.Device;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
@IdClass(ModuleConfigurationId.class)
public class ModuleConfiguration {
    @Id
    @ManyToOne
    @JoinColumn(name="device", nullable = false)
    private Device device;
    @Id
    private Long slot;
    @Id
    @ManyToOne
    @JoinColumn(name="sensor", nullable = false)
    private SensorType sensor;

    private boolean useCustom = false;

    private double min;
    private double max;

    public ModuleConfiguration() {}

    public ModuleConfiguration(Device device, Long slot, SensorType sensor, boolean useCustom, double min, double max) {
        this.device = device;
        this.slot = slot;
        this.sensor = sensor;
        this.useCustom = useCustom;
        this.min = min;
        this.max = max;
    }

    // Getters e Setters
    public double getMin() {
        return min;
    }
    public void setMin(double min) {
        this.min = min;
    }
    public double getMax() {
        return max;
    }
    public void setMax(double max) {
        this.max = max;
    }
    public SensorType getSensor() {
        return sensor;
    }

    public void setSensor(SensorType sensor) {
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

    public boolean getUseCustom() {
        return useCustom;
    }

    public void setUseCustom(boolean useCustom) {
        this.useCustom = useCustom;
    }
        
}
