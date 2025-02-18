package com.tap.unict.iotleaf.controllers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tap.unict.iotleaf.models.SensorType;
import com.tap.unict.iotleaf.models.Api.ApiResponse;
import com.tap.unict.iotleaf.models.Api.DeviceRegisterRequest;
import com.tap.unict.iotleaf.models.Configurations.ApiConfiguration;
import com.tap.unict.iotleaf.models.Device.Device;
import com.tap.unict.iotleaf.models.Modules.Module;
import com.tap.unict.iotleaf.models.Modules.ModuleConfig;
import com.tap.unict.iotleaf.models.Modules.ModuleConfiguration;
import com.tap.unict.iotleaf.models.Modules.ModuleConfigurationId;
import com.tap.unict.iotleaf.models.Modules.ModuleId;
import com.tap.unict.iotleaf.models.Plant.Plant;
import com.tap.unict.iotleaf.models.Plant.PlantBaseConfiguration;
import com.tap.unict.iotleaf.models.Plant.PlantBaseConfigurationId;
import com.tap.unict.iotleaf.repositories.DeviceRepository;
import com.tap.unict.iotleaf.repositories.ModuleConfigurationRepository;
import com.tap.unict.iotleaf.repositories.ModuleRepository;
import com.tap.unict.iotleaf.repositories.PlantBaseConfigurationRepository;
import com.tap.unict.iotleaf.repositories.SensorTypeRepository;


@RestController
@RequestMapping("/api")
public class ApiController {

    private ModuleRepository moduleRepository;
    private ModuleConfigurationRepository moduleConfigurationRepository;
    private DeviceRepository deviceRepository;
    private PlantBaseConfigurationRepository plantBaseConfigurationRepository;
    private SensorTypeRepository sensorTypeRepository;

    private boolean checkIfModuleExist(Device device, Long slot){
        return moduleRepository.existsById(new ModuleId(device, slot));
    }

    private ApiConfiguration getOrCreateModuleConfiguration(Device d, Long slot, SensorType s, Plant p) {
        ModuleConfiguration mc = null;
        ModuleConfigurationId mci = new ModuleConfigurationId(d,slot,s);
        PlantBaseConfigurationId pbci = new PlantBaseConfigurationId(p,s);
        if(moduleConfigurationRepository.existsById(mci)){
            mc = moduleConfigurationRepository.getReferenceById(mci);
        }
        
        if(mc == null || !mc.getUseCustom()){
            if(plantBaseConfigurationRepository.existsById(pbci)){
                PlantBaseConfiguration pbc = plantBaseConfigurationRepository.getReferenceById(pbci);
                mc = new ModuleConfiguration(d,slot,s,false,pbc.getMin(),pbc.getMax());
            }else{
                mc = new ModuleConfiguration(d,slot,s,false,0,0);
            }
        }

        return new ApiConfiguration(mc.getSensor().getName(),mc.getMin(),mc.getMax(),mc.getSensor().getFormat());
    }

    public ApiController(ModuleRepository moduleRepository, ModuleConfigurationRepository moduleConfigurationRepository,
            DeviceRepository deviceRepository, PlantBaseConfigurationRepository plantBaseConfigurationRepository,
            SensorTypeRepository sensorTypeRepository) {
        this.moduleRepository = moduleRepository;
        this.moduleConfigurationRepository = moduleConfigurationRepository;
        this.deviceRepository = deviceRepository;
        this.plantBaseConfigurationRepository = plantBaseConfigurationRepository;
        this.sensorTypeRepository = sensorTypeRepository;
    }
    
    @GetMapping("/device/{id}/{slot}/getConfig")
    public ApiResponse getModuleConfig(@PathVariable Long id, @PathVariable Long slot) {
        try{
            Device d = deviceRepository.getReferenceById(id);
            Module m = new Module(d, slot, "Slot "+slot,false,false, null, 0L);
            if(checkIfModuleExist(d,slot)){m = moduleRepository.getReferenceById(new ModuleId(d,slot));}

            List<ApiConfiguration> configurations = new ArrayList<>();
            for(SensorType sensorType : sensorTypeRepository.findAll()){
                configurations.add(getOrCreateModuleConfiguration(d, slot, sensorType, m.getPlant()));
            }

            return new ApiResponse(null,new ModuleConfig(id,slot,configurations));
        }catch(Exception e){
            return new ApiResponse(e.getMessage(), null);
        }
    }

    @PostMapping("/device/register")
    public ApiResponse registerDevice(@RequestBody DeviceRegisterRequest req){
        try{
            Device saved = deviceRepository.save(new Device(
                null,
                "New device",
                req.getIrrigation(),
                LocalDateTime.now(),
                req.getModel(),
                req.getSlots(),
                req.getNetwork(),
                req.getIpAddress(),
                req.getMacAddress(),
                req.getFirmware(),
                req.getUptime()
            ));

            return new ApiResponse(saved.getId()==null?"Error on create device":null,saved);
        }catch(Exception e){
            return new ApiResponse(e.getMessage(), null);
        }
    }
    
}
