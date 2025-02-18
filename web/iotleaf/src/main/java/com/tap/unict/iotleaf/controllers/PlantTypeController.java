package com.tap.unict.iotleaf.controllers;

import java.io.PrintWriter;
import java.io.StringWriter;
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
import com.tap.unict.iotleaf.models.Plant.PlantType;
import com.tap.unict.iotleaf.repositories.PlantTypeRepository;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("plantTypes")
public class PlantTypeController {
    private PlantTypeRepository plantTypeRepository;

    public PlantTypeController(PlantTypeRepository plantTypeRepository) {
        this.plantTypeRepository = plantTypeRepository;
    }

    private boolean checkIfPlantTypeExist(Long id){
        return plantTypeRepository.existsById(id);
    }

    private void setNavPage(PlantType pt, Model m){
        if(pt != null){
            if(pt.getId() == null){
                m.addAttribute("activePage", new ActivePage("views", "New Plant type","plantTypes",List.of(
                    new PathUrl("plantTypes","PlantTypes", "/plantTypes"),
                    new PathUrl("plantTypes","New", "/plantTypes/new")
                )));
            }else{
                m.addAttribute("activePage", new ActivePage("views", "Plant Type: "+pt.getName(),"plantTypes",List.of(
                    new PathUrl("plantTypes","PlantTypes", "/plantTypes"),
                    new PathUrl("plantTypes",pt.getName(), "/plantTypes/"+pt.getId())
                )));
            }
        }else{
            m.addAttribute("activePage", new ActivePage("views", "Plant Type's list","plantTypes",List.of(
                new PathUrl("plantTypes","Plant type's", "/plantTypes")
            )));
        }
        m.addAttribute("subPages", SubMenu.getViewsSubMenu());
    }

    @GetMapping("")
    public String list(
        @RequestParam(required = false) Long id,
        @RequestParam(required = false) String name,
        Model model
    ){
        setNavPage(null,model);
        try{
            Map<String,Object> filter = new HashMap<>();
            filter.put("id",id);
            filter.put("name",name);
            model.addAttribute("filter", filter);
            
            model.addAttribute("plantTypes", plantTypeRepository.searchPlantTypes(id,name));
            return "plantTypes/list";
        }catch(Exception e){
            model.addAttribute("error", getStackTrace(e));
            return "errors/503";
        }
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Long id,Model model){
        setNavPage(null, model);
        try{
            if(!checkIfPlantTypeExist(id)){return "errors/404";}
            PlantType pt = plantTypeRepository.getReferenceById(id);
            setNavPage(pt, model);
            
            model.addAttribute("plantType", pt);
            return "plantTypes/view";
        }catch(Exception e){
            model.addAttribute("error", getStackTrace(e));
            return "errors/503";
        }
    }

    @GetMapping("/new")
    public String add(Model model){
        setNavPage(null, model);
        try{
            PlantType pt = new PlantType(null,"New Plant type");
            setNavPage(pt, model);
            
            model.addAttribute("plantType", pt);
            return "plantTypes/view";
        }catch(Exception e){
            model.addAttribute("error", getStackTrace(e));
            return "errors/503";
        }
    }
    
    @PostMapping("/{id}/save")
    public String save(@PathVariable Long id, @ModelAttribute PlantType plantType, Model model){
        setNavPage(null,model);
        try{
            if(!checkIfPlantTypeExist(id)){return "errors/404";}
            setNavPage(plantType, model);
            plantType.setId(id);
            plantTypeRepository.save(plantType);
            return "redirect:/plantTypes/"+id;
        }catch(Exception e){
            model.addAttribute("error", getStackTrace(e));
            return "errors/503";
        }
    }

    @PostMapping("save")
    public String create(@ModelAttribute PlantType plantType, Model model){
        setNavPage(null, model);
        try{
            plantTypeRepository.save(plantType);
            return "redirect:/plantTypes";
        }catch(Exception e){
            model.addAttribute("error", getStackTrace(e));
            return "errors/503";
        }
    }

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id, Model model){
        setNavPage(null, model);
        try{
            if(!checkIfPlantTypeExist(id)){return "errors/404";}
            plantTypeRepository.delete(plantTypeRepository.getReferenceById(id));
            return "redirect:/plantTypes";
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
