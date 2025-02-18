package com.tap.unict.iotleaf;

import java.io.Serializable;

public class SensorAlert implements Serializable{
    private Long device_id;
    private Long slot;
    private String sensor;
    private double secureValue;
    public SensorAlert(Long device_id, Long slot, String sensor, double secureValue) {
        this.device_id = device_id;
        this.slot = slot;
        this.sensor = sensor;
        this.secureValue = secureValue;
    }
    public Long getSlot() {
        return slot;
    }
    public void setSlot(Long slot) {
        this.slot = slot;
    }
    public Long getDevice_id() {
        return device_id;
    }
    public void setDevice_id(Long device_id) {
        this.device_id = device_id;
    }
    public String getSensor() {
        return sensor;
    }
    public void setSensor(String sensor) {
        this.sensor = sensor;
    }
    public double getSecureValue() {
        return secureValue;
    }
    public void setSecureValue(double secureValue) {
        this.secureValue = secureValue;
    }
       
}
