package com.tap.unict.iotleaf.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tap.unict.iotleaf.models.Modules.Module;
import com.tap.unict.iotleaf.models.Device.Device;
import com.tap.unict.iotleaf.models.Modules.ModuleId;


public interface ModuleRepository extends JpaRepository<Module, ModuleId>{

    @Query("SELECT m FROM Module m WHERE " +
      "(m.device = :device) AND " +
      "(:name IS NULL OR m.name LIKE %:name%) AND " +
      "(:slot IS NULL OR m.slot = :slot) AND " +
      "(:plant IS NULL OR m.plant = :plant) AND " +
      "(:status IS NULL OR m.status = :status) AND " +
      "(:irrigation IS NULL OR m.irrigation = :irrigation)")
   List<Module> searchModules(
      @Param("device") Device device,
      @Param("name") String name,
      @Param("slot") Long slot,
      @Param("plant") Long plant,
      @Param("status") Boolean status,
      @Param("irrigation") Boolean irrigation
   );
}
