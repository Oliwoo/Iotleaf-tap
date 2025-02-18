package com.tap.unict.iotleaf.models.Modules;

import com.tap.unict.iotleaf.models.Device.Device;
import com.tap.unict.iotleaf.models.Plant.Plant;
import jakarta.persistence.*;

@Entity
@IdClass(ModuleId.class)
public class Module {
    @Id
    @ManyToOne
    @JoinColumn(name="device", nullable = false)
    private Device device;
    @Id
    @Column(nullable = false)
    private Long slot;

    private String name;
    private boolean status = false;
    private boolean irrigation = false;

    @ManyToOne
    @JoinColumn(name="plant", nullable = true)
    private Plant plant = null;

    private Long age = 0L;

    public Module() {}

    public Module(Device device, Long slot, String name, boolean status, boolean irrigation, Plant plant, Long age) {
        this.device = device;
        this.slot = slot;
        this.name = name;
        this.status = status;
        this.irrigation = irrigation;
        this.plant = plant;
        this.age = age;
    }

    // Getters e Setters
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public boolean getStatus() {
        return status;
    }
    public void setStatus(boolean status) {
        this.status = status;
    }
    public boolean getIrrigation() {
        return irrigation;
    }
    public void setIrrigation(boolean irrigation) {
        this.irrigation = irrigation;
    }
    public Plant getPlant() {
        return plant;
    }
    public void setPlant(Plant plant) {
        this.plant = plant;
    }
    public Long getAge() {
        return age;
    }
    public void setAge(Long age) {
        this.age = age;
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
    
}
