package com.tap.unict.iotleaf.models.Api;

public class DeviceRegisterRequest {
    private String model;
    private Boolean irrigation;
    private Integer slots;
    private String network;
    private String ipAddress;
    private String macAddress;
    private String firmware;
    private Long uptime;
    public DeviceRegisterRequest() {
    }
    public DeviceRegisterRequest(Boolean irrigation, String model, Integer slots, String network, String ipAddress, String macAddress,
            String firmware, Long uptime) {
        this.model = model;
        this.irrigation = irrigation;
        this.slots = slots;
        this.network = network;
        this.ipAddress = ipAddress;
        this.macAddress = macAddress;
        this.firmware = firmware;
        this.uptime = uptime;
    }
    public String getModel() {
        return model!=null?model:"Not defined";
    }
    public void setModel(String model) {
        this.model = model;
    }
    public Integer getSlots() {
        return slots;
    }
    public void setSlots(Integer slots) {
        this.slots = slots;
    }
    public String getNetwork() {
        return network!=null?network:"Not defined";
    }
    public void setNetwork(String network) {
        this.network = network;
    }
    public String getIpAddress() {
        return ipAddress!=null?ipAddress:"Not defined";
    }
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    public String getMacAddress() {
        return macAddress!=null?macAddress:"Not defined";
    }
    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }
    public String getFirmware() {
        return firmware!=null?firmware:"Not defined";
    }
    public void setFirmware(String firmware) {
        this.firmware = firmware;
    }
    public Boolean getIrrigation() {
        return irrigation!=null?irrigation:false;
    }
    public void setIrrigation(Boolean irrigation) {
        this.irrigation = irrigation;
    }
    public Long getUptime() {
        return uptime!=null?uptime:0L;
    }
    public void setUptime(Long uptime) {
        this.uptime = uptime;
    }    
}
