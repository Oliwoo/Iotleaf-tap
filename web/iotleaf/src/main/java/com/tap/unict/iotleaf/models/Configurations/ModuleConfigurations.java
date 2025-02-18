package com.tap.unict.iotleaf.models.Configurations;

import java.util.HashMap;
import java.util.Map;

import com.tap.unict.iotleaf.models.Modules.ModuleConfiguration;

public class ModuleConfigurations {
    private Map<Long, ModuleConfiguration> configurations = new HashMap<>();

    public Map<Long, ModuleConfiguration> getConfigurations() {
        return configurations;
    }

    public void setConfigurations(Map<Long, ModuleConfiguration> configurations) {
        this.configurations = configurations;
    }
    
}
