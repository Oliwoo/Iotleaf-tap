package com.tap.unict.iotleaf.controllers;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.tap.unict.iotleaf.models.ActivePage;
import com.tap.unict.iotleaf.models.PathUrl;
import com.tap.unict.iotleaf.models.SensorType;
import com.tap.unict.iotleaf.models.SubMenu;
import com.tap.unict.iotleaf.models.Configurations.ModuleConfigurations;
import com.tap.unict.iotleaf.models.Device.Device;
import com.tap.unict.iotleaf.models.Plant.Plant;
import com.tap.unict.iotleaf.models.Plant.PlantBaseConfiguration;
import com.tap.unict.iotleaf.models.Plant.PlantBaseConfigurationId;
import com.tap.unict.iotleaf.models.Modules.Module;
import com.tap.unict.iotleaf.models.Modules.ModuleConfiguration;
import com.tap.unict.iotleaf.models.Modules.ModuleConfigurationId;
import com.tap.unict.iotleaf.models.Modules.ModuleId;
import com.tap.unict.iotleaf.repositories.DeviceRepository;
import com.tap.unict.iotleaf.repositories.ModuleConfigurationRepository;
import com.tap.unict.iotleaf.repositories.ModuleRepository;
import com.tap.unict.iotleaf.repositories.PlantBaseConfigurationRepository;
import com.tap.unict.iotleaf.repositories.PlantRepository;
import com.tap.unict.iotleaf.repositories.SensorTypeRepository;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("modules")
public class ModuleController {
    private DeviceRepository deviceRepository;
    private PlantRepository plantRepository;
    private ModuleRepository moduleRepository;
    private SensorTypeRepository sensorTypeRepository;
    private PlantBaseConfigurationRepository plantBaseConfigurationRepository;
    private ModuleConfigurationRepository moduleConfigurationRepository;

    public ModuleController(DeviceRepository deviceRepository, PlantRepository plantRepository,ModuleRepository moduleRepository,SensorTypeRepository sensorTypeRepository, ModuleConfigurationRepository moduleConfigurationRepository, PlantBaseConfigurationRepository plantBaseConfigurationRepository) {
        this.deviceRepository = deviceRepository;
        this.plantRepository = plantRepository;
        this.moduleRepository = moduleRepository;
        this.sensorTypeRepository = sensorTypeRepository;
        this.moduleConfigurationRepository = moduleConfigurationRepository;
        this.plantBaseConfigurationRepository = plantBaseConfigurationRepository;
    }

    private boolean checkIfDeviceExist(Long id){
        return deviceRepository.existsById(id);
    }
    private boolean checkIfModuleExist(Device device, Long slot){
        return moduleRepository.existsById(new ModuleId(device, slot));
    }

    private List<ModuleConfiguration> filterConfigurations(List<ModuleConfiguration> configurations, Map<String,Object> filters){
        return configurations.stream()
            .filter(configuration -> (filters.get("device") == null && filters.get("slot") == null) || (configuration.getDevice()!=null && configuration.getDevice().getId() == filters.get("device") && configuration.getSlot() == filters.get("slot")))
            .filter(configuration -> filters.get("useCustom") == null || configuration.getUseCustom() == (Boolean)filters.get("useCustom"))
            .filter(configuration -> filters.get("min") == null || configuration.getMin() == (Long)filters.get("min"))
            .filter(configuration -> filters.get("max") == null || configuration.getMax() == (Long) filters.get("max"))
            .collect(Collectors.toList());
    }

    private ModuleConfiguration getOrCreateModuleConfiguration(Device d, Long slot, SensorType s, Plant p) {
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
        return mc;
    }

    private void setPageNav(Module m, Device d, Long slot, Model model){
        if(m != null && d != null && slot != null){
            model.addAttribute("activePage", new ActivePage("views", "Module: "+m.getName(),"devices",List.of(
                new PathUrl("devices","Devices", "/devices"),
                new PathUrl("devices",d.getName(), "/devices/"+d.getId()),
                new PathUrl("devices",m.getName(),"/modules/"+d.getId()+"/"+slot)
            )));
        }else if(d != null){
            model.addAttribute("activePage", new ActivePage("views", "Device: "+d.getName(),"devices",List.of(
                new PathUrl("devices","Devices", "/devices"),
                new PathUrl("devices",d.getName(), "/devices/"+d.getId())
            )));
        }else{
            model.addAttribute("activePage", new ActivePage("views", "Device's list","devices",List.of(
                new PathUrl("devices","Devices", "/devices")
            )));
        }

        model.addAttribute("subPages", SubMenu.getViewsSubMenu());
    }

    @GetMapping("/{device}/{slot}")
    public String view(
        @PathVariable Long device,
        @PathVariable Long slot,
        @RequestParam(required = false) Long sensor,
        @RequestParam(required = false) Boolean useCustom,
        @RequestParam(required = false) Long min,
        @RequestParam(required = false) Long max,
        Model model){
        setPageNav(null, null, slot, model);
        try{
            Map<String,Object> filter = new HashMap<>();
            filter.put("sensor",sensor);
            filter.put("useCustom", useCustom);
            filter.put("min",min);
            filter.put("max",max);
            model.addAttribute("filter", filter);

            if(!checkIfDeviceExist(device) || deviceRepository.getReferenceById(device).getSlots() < slot){return "errors/404";}
            Device d = deviceRepository.getReferenceById(device);
            Module m = new Module(d, slot, "Slot "+slot,false,false, null, 0L);
            if(checkIfModuleExist(d,slot)){m = moduleRepository.getReferenceById(new ModuleId(d,slot));}
            setPageNav(m, d, slot, model);

            model.addAttribute("plants", plantRepository.findAll());
            model.addAttribute("module", m);
            
            List<SensorType> sensors = sensorTypeRepository.findAll();
            model.addAttribute("sensors", sensors);
            List<ModuleConfiguration> configurations = new ArrayList<>();
            for(SensorType sensorType : sensors){
                configurations.add(getOrCreateModuleConfiguration(d, slot, sensorType, m.getPlant()));
            }

            model.addAttribute("configurations", filterConfigurations(configurations,filter));
            
            return "modules/view";
        }catch(Exception e){
            model.addAttribute("error", getStackTrace(e));
            return "errors/503";
        }
    }

    @PostMapping("/{device}/{slot}/configurations")
    public String saveConfigurations(
        @PathVariable Long device,
        @PathVariable Long slot,
        @RequestParam(required = false) Long sensor,
        @RequestParam(required = false) Boolean useCustom,
        @RequestParam(required = false) Long min,
        @RequestParam(required = false) Long max,
        RedirectAttributes redirectAttributes,
        @ModelAttribute ModuleConfigurations moduleConfigurations,
        Model model
    ){
        setPageNav(null, null, slot, model);
        try{
            Map<String,Object> filter = new HashMap<>();
            filter.put("sensor",sensor);
            filter.put("useCustom", useCustom);
            filter.put("min",min);
            filter.put("max",max);
            model.addAttribute("filter", filter);
            
            if(!checkIfDeviceExist(device)){return "errors/404";}
            Device d = deviceRepository.getReferenceById(device);
            Module m = new Module(d,slot,"Slot "+slot,false,false, null, 0L);
            if(checkIfModuleExist(d,slot)){m = moduleRepository.getReferenceById(new ModuleId(d,slot));}

            setPageNav(m, d, slot, model);

            Map<Long, ModuleConfiguration> configurations = moduleConfigurations.getConfigurations();
            configurations.forEach((key,configuration) -> {
                configuration.setDevice(d);
                configuration.setSlot(slot);
                configuration.setSensor(sensorTypeRepository.getReferenceById(key));
            });
            if(!configurations.isEmpty()){moduleConfigurationRepository.saveAll(configurations.values());}
            
            model.addAttribute("plants", plantRepository.findAll());
            model.addAttribute("module", m);

            filter.forEach(redirectAttributes::addAttribute);
            return "redirect:/modules/"+device+"/"+slot;
        }catch(Exception e){
            model.addAttribute("error", getStackTrace(e));
            return "errors/503";
        }
    }
    
    @PostMapping("/{device}/{slot}/save")
    public String save(@PathVariable Long device, @PathVariable Long slot, @RequestParam String name, @RequestParam Long plant, @RequestParam boolean status, @RequestParam boolean irrigation, Model model){
        setPageNav(null, null, slot, model);
        try{
            if(!checkIfDeviceExist(device)){return "errors/404";}
            Device d = deviceRepository.getReferenceById(device);
            Module m = new Module(d,slot,"Slot "+slot,false,false, new Plant(), 0L);
            if(checkIfModuleExist(d,slot)){m = moduleRepository.getReferenceById(new ModuleId(d,slot));}
            setPageNav(m, d, slot, model);

            if(plant!=null){m.setPlant(plantRepository.getReferenceById(plant));}
            m.setName(name);
            m.setStatus(status);
            m.setIrrigation(irrigation);
            moduleRepository.save(m);
            return "redirect:/modules/"+device+"/"+slot;
        }catch(Exception e){
            model.addAttribute("error", getStackTrace(e));
            return "errors/503";
        }
    }

    @GetMapping("/{device}/{slot}/delete")
    public String delete(@PathVariable Long device, @PathVariable Long slot, Model model){
        setPageNav(null, null, slot, model);
        try{
            if(!checkIfDeviceExist(device) || !checkIfModuleExist(deviceRepository.getReferenceById(device),slot)){return "errors/404";}
            moduleRepository.delete(moduleRepository.getReferenceById(new ModuleId(deviceRepository.getReferenceById(device),slot)));
            return "redirect:/devices";
        }catch(Exception e){
            model.addAttribute("error", getStackTrace(e));
            return "errors/503";
        }
        
    }

    private static String getStackTrace(final Throwable throwable) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
    }
    
}
