package com.tap.unict.iotleaf.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tap.unict.iotleaf.models.Plant.PlantType;

public interface PlantTypeRepository extends JpaRepository<PlantType,Long>{
    @Query("SELECT pt FROM PlantType pt WHERE " +
        "(:id IS NULL OR pt.id = :id) AND " +
        "(:name IS NULL OR pt.name LIKE %:name%)")
    List<PlantType> searchPlantTypes(
        @Param("id") Long id,
        @Param("name") String name
    );
}
