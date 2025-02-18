package com.tap.unict.iotleaf.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tap.unict.iotleaf.models.Plant.Plant;
import com.tap.unict.iotleaf.models.Plant.PlantBaseConfiguration;
import com.tap.unict.iotleaf.models.Plant.PlantBaseConfigurationId;

public interface PlantBaseConfigurationRepository extends JpaRepository<PlantBaseConfiguration, PlantBaseConfigurationId>{
    public List<PlantBaseConfiguration> getByPlant(Plant plant);
}
