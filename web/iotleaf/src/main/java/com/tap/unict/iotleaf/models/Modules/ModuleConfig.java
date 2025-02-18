package com.tap.unict.iotleaf.models.Modules;

import java.util.List;

import com.tap.unict.iotleaf.models.Configurations.ApiConfiguration;

public class ModuleConfig {
    private Long deviceId;
    private Long slot;
    private List<ApiConfiguration> sensors;

    public ModuleConfig(){}
    public ModuleConfig(Long deviceId, Long slot, List<ApiConfiguration> sensors) {
        this.deviceId = deviceId;
        this.slot = slot;
        this.sensors = sensors;
    }
    public Long getDeviceId() {
        return deviceId;
    }
    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }
    public Long getSlot() {
        return slot;
    }
    public void setSlot(Long slot) {
        this.slot = slot;
    }
    public List<ApiConfiguration> getSensors() {
        return sensors;
    }
    public void setSensors(List<ApiConfiguration> sensors) {
        this.sensors = sensors;
    }
}
