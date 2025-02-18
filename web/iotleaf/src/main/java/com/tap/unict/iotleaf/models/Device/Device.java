package com.tap.unict.iotleaf.models.Device;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.*;

@Entity
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(nullable = false)
    private boolean irrigation;
    @Column(nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime lastPing;
    @Column(nullable = false)
    private String model;
    @Column(nullable = false)
    private Integer slots;

    /* -- Connection info -- */
    @Column(nullable = false)
    private String network;
    @Column(nullable = false)
    private String ipAddress;
    @Column(nullable = false)
    private String macAddress;

    /* -- System info -- */
    @Column(nullable = false)
    private String firmware;
    private Long uptime = 0L;

    public Device() {
    }
    public Device(Long id, String name, boolean irrigation, LocalDateTime lastPing, String model, Integer slots, String network, String ipAddress,
            String macAddress, String firmware, Long uptime) {
        this.id = id;
        this.name = name;
        this.irrigation = irrigation;
        this.lastPing = lastPing;
        this.model = model;
        this.slots = slots;
        this.network = network;
        this.ipAddress = ipAddress;
        this.macAddress = macAddress;
        this.firmware = firmware;
        this.uptime = uptime;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public boolean isIrrigation() {
        return irrigation;
    }
    public void setIrrigation(boolean irrigation) {
        this.irrigation = irrigation;
    }
    public LocalDateTime getLastPing() {
        return lastPing;
    }
    public void setLastPing(LocalDateTime lastPing) {
        this.lastPing = lastPing;
    }
    public String getNetwork() {
        return network;
    }
    public void setNetwork(String network) {
        this.network = network;
    }
    public String getIpAddress() {
        return ipAddress;
    }
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    public String getMacAddress() {
        return macAddress;
    }
    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }
    public String getFirmware() {
        return firmware;
    }
    public void setFirmware(String firmware) {
        this.firmware = firmware;
    }
    public Long getUptime() {
        return uptime;
    }
    public String getUptimeStr(){
        long totalSeconds = this.uptime;
        long secondsInMinute = 60;
        long secondsInHour = 3600;
        long secondsInDay = 86400;
        long secondsInYear = 31536000;
        
        // Calcolo anni, giorni, minuti e secondi rimanenti
        long years = totalSeconds / secondsInYear;
        totalSeconds %= secondsInYear;
        
        long days = totalSeconds / secondsInDay;
        totalSeconds %= secondsInDay;
        
        long hours = totalSeconds / secondsInHour;
        totalSeconds %= secondsInHour;
        
        long minutes = totalSeconds / secondsInMinute; // conversione minuti
        totalSeconds %= secondsInMinute;
        
        long seconds = totalSeconds; // rimangono i secondi
        
        // Costruire la stringa di risultato
        StringBuilder result = new StringBuilder();
        
        if (years > 0) {
            result.append(years).append(" y ");
        }
        if (days > 0) {
            result.append(days).append(" d ");
        }
        if (hours > 0) {
            result.append(hours).append(" h ");
        }
        if (minutes > 0) {
            result.append(minutes).append(" min ");
        }
        result.append(seconds).append(" s");
        
        return result.toString();
    }
    public void setUptime(Long uptime) {
        this.uptime = uptime;
    }
    public String getModel() {
        return model;
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
    public boolean getStatus(){
        return !lastPing.isBefore(LocalDateTime.now().minusMinutes(5));
    }
}
