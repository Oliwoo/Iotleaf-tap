package com.tap.unict.iotleaf.models.Configurations;

import java.util.HashMap;
import java.util.Map;

import com.tap.unict.iotleaf.models.Plant.PlantBaseConfiguration;

public class PlantConfigurations {
    private Map<Long, PlantBaseConfiguration> configurations = new HashMap<>();

    public Map<Long, PlantBaseConfiguration> getConfigurations() {
        return configurations;
    }

    public void setConfigurations(Map<Long, PlantBaseConfiguration> configurations) {
        this.configurations = configurations;
    }
    
}
