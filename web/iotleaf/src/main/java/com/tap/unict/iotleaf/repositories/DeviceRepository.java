package com.tap.unict.iotleaf.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tap.unict.iotleaf.models.Device.Device;

public interface DeviceRepository extends JpaRepository<Device, Long> {
   @Query(value = "SELECT d.* FROM device d WHERE " +
      "(:id IS NULL OR d.id = :id) AND " +
      "(:name IS NULL OR d.name LIKE %:name%) AND " +
      "(:slots IS NULL OR d.slots = :slots) AND " +
      "(:model IS NULL OR d.model LIKE %:model%) AND " +
      "(:firmware IS NULL OR d.firmware LIKE %:firmware%) AND " +
      "(:irrigation IS NULL OR d.irrigation = :irrigation)",
   nativeQuery = true)
   List<Device> searchDevices(
      @Param("id") Long id,
      @Param("name") String name,
      @Param("slots") Long slots,
      @Param("model") String model,
      @Param("firmware") String firmware,
      @Param("irrigation") Boolean irrigation
   );

}