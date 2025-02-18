package com.tap.unict.iotleaf.controllers;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.tap.unict.iotleaf.models.ActivePage;
import com.tap.unict.iotleaf.models.PathUrl;
import com.tap.unict.iotleaf.models.SubMenu;
import com.tap.unict.iotleaf.models.Device.Device;
import com.tap.unict.iotleaf.models.Plant.Plant;
import com.tap.unict.iotleaf.models.Modules.Module;
import com.tap.unict.iotleaf.models.Modules.ModuleId;
import com.tap.unict.iotleaf.repositories.DeviceRepository;
import com.tap.unict.iotleaf.repositories.ModuleRepository;
import com.tap.unict.iotleaf.repositories.PlantRepository;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("devices")
public class DeviceController {
    private DeviceRepository deviceRepository;
    private PlantRepository plantRepository;
    private ModuleRepository moduleRepository;

    public DeviceController(DeviceRepository deviceRepository, PlantRepository plantRepository,ModuleRepository moduleRepository) {
        this.deviceRepository = deviceRepository;
        this.plantRepository = plantRepository;
        this.moduleRepository = moduleRepository;
    }

    private List<Module> filterModules(List<Module> modules, Map<String,Object> filters){
        return modules.stream()
            .filter(module -> filters.get("name") == null || module.getName().contains((String) filters.get("name")))
            .filter(module -> filters.get("slot") == null || module.getSlot() == (long) filters.get("slot"))
            .filter(module -> filters.get("plant") == null || (module.getPlant() != null && module.getPlant().getId() == (long) filters.get("plant")))
            .filter(module -> filters.get("status") == null || module.getStatus() == (boolean) filters.get("status"))
            .filter(module -> filters.get("irrigation") == null || module.getIrrigation() == (boolean) filters.get("irrigation"))
            .toList();
    }

    private boolean checkIfModuleExist(Device d, Long s){
        return moduleRepository.existsById(new ModuleId(d, s));
    }
    private boolean checkIfDeviceExist(Long id){
        return deviceRepository.existsById(id);
    }

    private void setPageNav(Device d, Model model){
        if(d != null){
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

    @GetMapping("")
    public String list(
        @RequestParam(required = false) Long id,
        @RequestParam(required = false) String name,
        @RequestParam(required = false) Long slots,
        @RequestParam(required = false) String model,
        @RequestParam(required = false) String firmware,
        @RequestParam(required = false) Boolean irrigation,
        Model modelRet
    ) {
        setPageNav(null, modelRet);
        try{
            Map<String,Object> filter = new HashMap<>();
            filter.put("id",id);
            filter.put("name",name);
            filter.put("slots",slots);
            filter.put("model",model);
            filter.put("firmware",firmware);
            filter.put("irrigation",irrigation);

            modelRet.addAttribute("filter", filter);
            modelRet.addAttribute("devices", deviceRepository.searchDevices(id, name, slots, model, firmware, irrigation));
            return "devices/list";
        }catch(Exception e){
            modelRet.addAttribute("error",getStackTrace(e));
            return "errors/503";
        }
    }

    @GetMapping("/{deviceID}")
    public String view(
        @PathVariable Long deviceID,
        @RequestParam(required = false) String name,
        @RequestParam(required = false) Boolean status,
        @RequestParam(required = false) Long slot,
        @RequestParam(required = false) Long plant,
        @RequestParam(required = false) Boolean irrigation,
        Model model
    ){
        setPageNav(null, model);
        try{
            if(!checkIfDeviceExist(deviceID)){return "errors/404";}

            Device d = deviceRepository.getReferenceById(deviceID);
            setPageNav(d, model);

            Map<String,Object> filter = new HashMap<>();
            filter.put("name",name);
            filter.put("status", status);
            filter.put("slot",slot);
            filter.put("plant",plant);
            filter.put("irrigation",irrigation);
            model.addAttribute("filter", filter);

            List<Module> modules = new ArrayList<>();
            for(Long i=0L;i<d.getSlots(); i++){
                Module m = new Module(d,i, "Slot "+i, false, false, new Plant(), 0L);
                if(checkIfModuleExist(d,i)){m = moduleRepository.getReferenceById(new ModuleId(d,i));}
                modules.add(m);
            }
            
            model.addAttribute("modules",modules);
            model.addAttribute("modulesFiltered", filterModules(modules,filter));
            model.addAttribute("plants", plantRepository.findAll());
            model.addAttribute("device", d);
            return "devices/view";
        }catch(Exception e){
            model.addAttribute("error",getStackTrace(e));
            return "errors/503";
        }
    }
    
    @PostMapping("/{id}/save")
    public String save(@PathVariable Long id, @RequestParam String name, @RequestParam boolean irrigation, Model model){
        setPageNav(null, model);
        try{
            if(!checkIfDeviceExist(id)){return "errors/404";}

            Device d = deviceRepository.getReferenceById(id);
            d.setName(name);
            d.setIrrigation(irrigation);
            deviceRepository.save(d);
            return "redirect:/devices/"+id;
        }catch(Exception e){
            model.addAttribute("error",getStackTrace(e));
            return "errors/503";
        }
    }

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id, Model model){
        setPageNav(null, model);
        try{
            if(!checkIfDeviceExist(id)){return "errors/404";}
            deviceRepository.delete(deviceRepository.getReferenceById(id));
            return "redirect:/devices";
        }catch(Exception e){
            model.addAttribute("error",getStackTrace(e));
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
