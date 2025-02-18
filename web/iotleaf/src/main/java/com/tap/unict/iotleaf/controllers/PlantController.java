package com.tap.unict.iotleaf.controllers;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.tap.unict.iotleaf.models.ActivePage;
import com.tap.unict.iotleaf.models.PathUrl;
import com.tap.unict.iotleaf.models.SensorType;
import com.tap.unict.iotleaf.models.SubMenu;
import com.tap.unict.iotleaf.models.Configurations.PlantConfigurations;
import com.tap.unict.iotleaf.models.Plant.Plant;
import com.tap.unict.iotleaf.models.Plant.PlantBaseConfiguration;
import com.tap.unict.iotleaf.models.Plant.PlantBaseConfigurationId;
import com.tap.unict.iotleaf.models.Plant.PlantType;
import com.tap.unict.iotleaf.repositories.PlantBaseConfigurationRepository;
import com.tap.unict.iotleaf.repositories.PlantRepository;
import com.tap.unict.iotleaf.repositories.PlantTypeRepository;
import com.tap.unict.iotleaf.repositories.SensorTypeRepository;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("plants")
public class PlantController {
    private PlantRepository plantRepository;
    private PlantTypeRepository plantTypeRepository;
    private PlantBaseConfigurationRepository plantBaseConfigurationRepository;
    private SensorTypeRepository sensorTypeRepository;
    
    @Value("${upload.path}")
    private String uploadDir;

    public PlantController(PlantRepository plantRepository, PlantTypeRepository plantTypeRepository, PlantBaseConfigurationRepository plantBaseConfigurationRepository, SensorTypeRepository sensorTypeRepository){
        this.plantRepository = plantRepository;
        this.plantTypeRepository = plantTypeRepository;
        this.plantBaseConfigurationRepository = plantBaseConfigurationRepository;
        this.sensorTypeRepository = sensorTypeRepository;
    }

    private boolean checkIfPlantExist(Long id){
        return plantRepository.existsById(id);
    }
    private boolean checkIfPlantTypeExist(Long id){
        return plantTypeRepository.existsById(id);
    }
    private List<PlantBaseConfiguration> filterConfigurations(List<PlantBaseConfiguration> configurations, Map<String,Object> filters){
        return configurations.stream()
            .filter(configuration -> filters.get("sensor") == null || (configuration.getSensor()!=null && configuration.getSensor().getId() == filters.get("sensor")))
            .filter(configuration -> filters.get("min") == null || configuration.getMin() == (Long)filters.get("min"))
            .filter(configuration -> filters.get("max") == null || configuration.getMax() == (Long) filters.get("max"))
            .toList();
    }

    private String uploadImg(MultipartFile file, String name, String uploadDir) {
        if(file.isEmpty()) return null;
        try {
            Files.createDirectories(Paths.get(uploadDir));
            String originalFilename = file.getOriginalFilename();
            String extension = StringUtils.getFilenameExtension(originalFilename);
            if (extension == null) return null;

            String fileName = name + "." + extension;
            Path filePath = Paths.get(uploadDir, fileName);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        }catch(IOException e){
            return null;
        }
    }

    private PlantBaseConfiguration getOrCreatePlantBaseConfiguration(Plant p, SensorType s) {
        return plantBaseConfigurationRepository.findById(new PlantBaseConfigurationId(p, s))
        .orElse(new PlantBaseConfiguration(p, s, 0, 0));
    }    

    private void setNavPage(Plant p, Model m){
        if(p != null){
            if(p.getId() == null){
                m.addAttribute("activePage", new ActivePage("views", "New plant","plants",List.of(
                    new PathUrl("plants","Plants", "/plants"),
                    new PathUrl("plants","New", "/plants/new")
                )));
            }else{
                m.addAttribute("activePage", new ActivePage("views", "Plant: "+p.getName(),"plants",List.of(
                    new PathUrl("plants","Plants", "/plants"),
                    new PathUrl("plants",p.getName(), "/plants/"+p.getId())
                )));
            }
        }else{
            m.addAttribute("activePage", new ActivePage("views", "Plant's list","plants",List.of(
                new PathUrl("plants","Plants", "/plants")
            )));
        }

        m.addAttribute("subPages", SubMenu.getViewsSubMenu());
    }

    @GetMapping("")
    public String list(
        @RequestParam(required = false) Long id,
        @RequestParam(required = false) String name,
        @RequestParam(required = false) Integer germogliationTime,
        @RequestParam(required = false) Boolean outdoor,
        @RequestParam(required = false) Long plantType,
        @RequestParam(required = false) String plantInfo,
        @RequestParam(required = false) String allevationInfo,
        Model model
    ) {
        try{
            Map<String,Object> filter = new HashMap<>();
            filter.put("id",id);
            filter.put("name",name);
            filter.put("germogliationTime",germogliationTime);
            filter.put("outdoor",outdoor);
            filter.put("plantType",plantType);
            filter.put("plantInfo", plantInfo);
            filter.put("allevationInfo", allevationInfo);
            PlantType plantFilter = null;
            if(plantType != null && checkIfPlantTypeExist(plantType)){plantFilter = plantTypeRepository.getReferenceById(plantType);}

            model.addAttribute("filter", filter);
            model.addAttribute("activePage", new ActivePage("views", "Plant's List","plants",List.of(
                new PathUrl("plants","Plants", "/plants")
            )));
            model.addAttribute("subPages", SubMenu.getViewsSubMenu());
            model.addAttribute("plantTypes", plantTypeRepository.findAll());
            model.addAttribute("plants", plantRepository.searchPlants(id, name, germogliationTime, outdoor, plantFilter, plantInfo, allevationInfo));
            return "plants/list";
        }catch(Exception e){
            model.addAttribute("error",getStackTrace(e));
            return "errors/503";
        }
    }

    @GetMapping("/{id}")
    public String view(
        @PathVariable Long id,
        @RequestParam(required = false) Long sensor,
        @RequestParam(required = false) Long min,
        @RequestParam(required = false) Long max,
        Model model
    ){
        setNavPage(null, model);
        try{
            Map<String,Object> filter = new HashMap<>();
            filter.put("sensor",sensor);
            filter.put("min",min);
            filter.put("max",max);
            model.addAttribute("filter", filter);

            if(!checkIfPlantExist(id)){return "errors/404";}
            Plant p = plantRepository.getReferenceById(id);
            setNavPage(p, model);

            model.addAttribute("plantTypes", plantTypeRepository.findAll());
            model.addAttribute("plant", p);

            List<SensorType> sensors = sensorTypeRepository.findAll();
            model.addAttribute("sensors", sensors);
            List<PlantBaseConfiguration> configurations = sensors.stream()
            .map(sensorType -> getOrCreatePlantBaseConfiguration(p, sensorType))
            .toList();

            model.addAttribute("configurations", filterConfigurations(configurations,filter));
            return "plants/view";
        }catch(Exception e){
            model.addAttribute("error",getStackTrace(e));
            return "errors/503";
        }
    }

    @GetMapping("/new")
    public String add(
        @RequestParam(required = false) Long sensor,
        @RequestParam(required = false) Long min,
        @RequestParam(required = false) Long max,
        Model model
    ){
        setNavPage(null, model);
        try{
            Map<String,Object> filter = new HashMap<>();
            filter.put("sensor",sensor);
            filter.put("min",min);
            filter.put("max",max);
            model.addAttribute("filter", filter);

            Plant p = new Plant(null,"New Plant",1,"/images/no_image.png",false,null,"-","-");
            setNavPage(p, model);

            model.addAttribute("plantTypes", plantTypeRepository.findAll());
            model.addAttribute("plant", p);

            return "plants/view";
        }catch(Exception e){
            model.addAttribute("error",getStackTrace(e));
            return "errors/503";
        }
    }

    @PostMapping("/{id}/configurations")
    public String saveConfigurations(
        @PathVariable Long id,
        @RequestParam(required = false) Long sensor,
        @RequestParam(required = false) Long min,
        @RequestParam(required = false) Long max,
        RedirectAttributes redirectAttributes,
        @ModelAttribute PlantConfigurations plantConfigurations,
        Model model
    ){
        setNavPage(null, model);
        try{
            Map<String,Object> filter = new HashMap<>();
            filter.put("sensor",sensor);
            filter.put("min",min);
            filter.put("max",max);
            model.addAttribute("filter", filter);
            
            if(!checkIfPlantExist(id)){return "errors/404";}
            Plant p = plantRepository.getReferenceById(id);
            setNavPage(p, model);

            Map<Long, PlantBaseConfiguration> configurations = plantConfigurations.getConfigurations();
            configurations.forEach((key, conf) -> {
                conf.setSensor(sensorTypeRepository.getReferenceById(key));
                conf.setPlant(p);
            });
            if(!configurations.isEmpty()){plantBaseConfigurationRepository.saveAll(configurations.values());}

            model.addAttribute("plantTypes", plantTypeRepository.findAll());
            model.addAttribute("plant", p);

            filter.forEach(redirectAttributes::addAttribute);
            return "redirect:/plants/" + id;
        }catch(Exception e){
            model.addAttribute("error", getStackTrace(e));
            return "errors/503";
        }
    }

    @PostMapping("/{id}/save")
    public String save(@PathVariable Long id, @ModelAttribute Plant plant, @RequestParam("image") MultipartFile image, Model model){
        setNavPage(plant, model);
        try{
            if(!checkIfPlantExist(id)) return "errors/404";

            String fileName = uploadImg(image, plant.getId().toString(), uploadDir + "/plants");
            if(fileName != null){plant.setImgPath("/uploads/plants/" + fileName);}

            plantRepository.save(plant);
            return "redirect:/plants/" + id;
        }catch(Exception e) {
            model.addAttribute("error", getStackTrace(e));
            return "errors/503";
        }
    }

    @PostMapping("/save")
    public String create(@ModelAttribute Plant plant, @RequestParam("image") MultipartFile image, Model model){
        setNavPage(null, model);
        try{
            plant = plantRepository.save(plant);

            String fileName = uploadImg(image, plant.getId().toString(), uploadDir + "/plants");
            if(fileName != null){plant.setImgPath("/uploads/plants/" + fileName);}

            plantRepository.save(plant);

            return "redirect:/plants";
        }catch(Exception e) {
            model.addAttribute("error", getStackTrace(e));
            return "errors/503";
        }
    }

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id, Model model){
        setNavPage(null, model);
        try{
            if(!checkIfPlantExist(id)){return "errors/404";}
            plantBaseConfigurationRepository.deleteAll(plantBaseConfigurationRepository.getByPlant(plantRepository.getReferenceById(id)));
            plantRepository.delete(plantRepository.getReferenceById(id));
            return "redirect:/plants";
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
