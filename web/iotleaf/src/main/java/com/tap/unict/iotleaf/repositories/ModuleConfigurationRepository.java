package com.tap.unict.iotleaf.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tap.unict.iotleaf.models.Modules.ModuleConfiguration;
import com.tap.unict.iotleaf.models.Modules.ModuleConfigurationId;

public interface ModuleConfigurationRepository extends JpaRepository<ModuleConfiguration, ModuleConfigurationId>{
    
}
