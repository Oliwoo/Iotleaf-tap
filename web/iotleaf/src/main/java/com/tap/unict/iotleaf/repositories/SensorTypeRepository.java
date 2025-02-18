package com.tap.unict.iotleaf.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tap.unict.iotleaf.models.SensorType;

public interface SensorTypeRepository extends JpaRepository<SensorType,Long>{
    @Query("SELECT st FROM SensorType st WHERE " +
        "(:id IS NULL OR st.id = :id) AND " +
        "(:name IS NULL OR st.name LIKE %:name%) AND "+
        "(:format IS NULL OR st.format LIKE %:format%)")
    List<SensorType> searchSensorTypes(
        @Param("id") Long id,
        @Param("name") String name,
        @Param("format") String format
    );
}
