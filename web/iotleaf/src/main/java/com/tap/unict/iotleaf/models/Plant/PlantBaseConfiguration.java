package com.tap.unict.iotleaf.models.Plant;

import com.tap.unict.iotleaf.models.SensorType;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
@IdClass(PlantBaseConfigurationId.class)
public class PlantBaseConfiguration {
    @Id
    @ManyToOne
    @JoinColumn(name="plant", nullable = false)
    private Plant plant;
    @Id
    @ManyToOne
    @JoinColumn(name="sensor", nullable = false)
    private SensorType sensor;
    private double min;
    private double max;
    public PlantBaseConfiguration() {
    }
    public PlantBaseConfiguration(Plant plant, SensorType sensor, double min, double max) {
        this.plant = plant;
        this.sensor = sensor;
        this.min = min;
        this.max = max;
    }
    public Plant getPlant() {
        return plant;
    }
    public void setPlant(Plant plant) {
        this.plant = plant;
    }
    public SensorType getSensor() {
        return sensor;
    }
    public void setSensor(SensorType sensor) {
        this.sensor = sensor;
    }
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
}
