package com.tap.unict.iotleaf.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tap.unict.iotleaf.models.Plant.Plant;
import com.tap.unict.iotleaf.models.Plant.PlantType;

public interface PlantRepository extends JpaRepository<Plant, Long>{
    @Query("SELECT p FROM Plant p WHERE " +
      "(:id IS NULL OR p.id = :id) AND " +
      "(:name IS NULL OR p.name LIKE %:name%) AND " +
      "(:germogliationTime IS NULL OR p.germogliationTime = :germogliationTime) AND " +
      "(:outdoor IS NULL OR p.outdoor = :outdoor) AND " +
      "(:plantType IS NULL OR p.type = :plantType) AND" +
      "(:plantInfo IS NULL OR p.plantInfo LIKE %:plantInfo%) AND " +
      "(:allevationInfo IS NULL OR p.allevationInfo LIKE %:allevationInfo%)")
   List<Plant> searchPlants(
      @Param("id") Long id,
      @Param("name") String name,
      @Param("germogliationTime") Integer germogliationTime,
      @Param("outdoor") Boolean outdoor,
      @Param("plantType") PlantType plantType,
      @Param("plantInfo") String plantInfo,
      @Param("allevationInfo") String allevationInfo
   );
}
