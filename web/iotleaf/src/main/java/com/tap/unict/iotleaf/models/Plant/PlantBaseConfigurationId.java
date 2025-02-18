package com.tap.unict.iotleaf.models.Plant;

import java.io.Serializable;
import java.util.Objects;

import com.tap.unict.iotleaf.models.SensorType;

public class PlantBaseConfigurationId implements Serializable {
    private Plant plant;
    private SensorType sensor;

    // Costruttore di default
    public PlantBaseConfigurationId() {}
    public PlantBaseConfigurationId(Plant plant, SensorType sensor) {
        this.plant = plant;
        this.sensor = sensor;
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

    // equals() e hashCode()
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlantBaseConfigurationId that = (PlantBaseConfigurationId) o;
        return Objects.equals(plant, that.plant) &&
               Objects.equals(sensor, that.sensor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(plant, sensor);
    }
}