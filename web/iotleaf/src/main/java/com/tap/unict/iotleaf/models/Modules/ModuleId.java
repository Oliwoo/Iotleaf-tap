package com.tap.unict.iotleaf.models.Modules;

import java.io.Serializable;
import java.util.Objects;

import com.tap.unict.iotleaf.models.Device.Device;

public class ModuleId implements Serializable {
    private Device device;
    private Long slot;

    public ModuleId() {}

    public ModuleId(Device device, Long slot) {
        this.device = device;
        this.slot = slot;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ModuleId that = (ModuleId) o;
        return Objects.equals(device, that.device) &&
               Objects.equals(slot, that.slot);
    }

    @Override
    public int hashCode() {
        return Objects.hash(device, slot);
    }

    // Getters e Setters
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
}
