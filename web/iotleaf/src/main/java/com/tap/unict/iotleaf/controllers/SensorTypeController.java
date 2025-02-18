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
import com.tap.unict.iotleaf.models.SensorType;
import com.tap.unict.iotleaf.models.SubMenu;
import com.tap.unict.iotleaf.repositories.SensorTypeRepository;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("sensorTypes")
public class SensorTypeController {
    private SensorTypeRepository sensorTypeRepository;

    public SensorTypeController(SensorTypeRepository sensorTypeRepository) {
        this.sensorTypeRepository = sensorTypeRepository;
    }

    private boolean checkIfSensorTypeExist(Long id){
        return sensorTypeRepository.existsById(id);
    }

    private void setNavPage(SensorType st, Model m){
        if(st != null){
            if(st.getId() == null){
                m.addAttribute("activePage", new ActivePage("views", "Add new sensor type","sensorTypes",List.of(
                    new PathUrl("sensorTypes","Sensor type's", "/sensorTypes"),
                    new PathUrl("sensorTypes","New", "/sensorTypes/new")
                )));
            }else{
                m.addAttribute("activePage", new ActivePage("views", "Sensor Type: "+st.getName(),"sensorTypes",List.of(
                    new PathUrl("sensorTypes","Sensor type's", "/sensorTypes"),
                    new PathUrl("sensorTypes",st.getName(), "/sensorTypes/"+st.getId())
                )));
            }
        }else{
            m.addAttribute("activePage", new ActivePage("views", "Sensor Type's list","sensorTypes",List.of(
                new PathUrl("sensorTypes","Sensor Type's", "/sensorTypes")
            )));
        }
        m.addAttribute("subPages", SubMenu.getViewsSubMenu());
    }

    @GetMapping("")
    public String list(
        @RequestParam(required = false) Long id,
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String format,
        Model model
    ){
        setNavPage(null, model);
        try{
            Map<String,Object> filter = new HashMap<>();
            filter.put("id",id);
            filter.put("name",name);
            filter.put("format",format);
            model.addAttribute("filter", filter);
            
            model.addAttribute("sensorTypes", sensorTypeRepository.searchSensorTypes(id,name,format));
            return "sensorTypes/list";
        }catch(Exception e){
            model.addAttribute("error", getStackTrace(e));
            return "errors/503";
        }
    }

    @GetMapping("/new")
    public String add(Model model){
        setNavPage(null, model);
        try{
            SensorType st = new SensorType(null,"New Sensor",null);
            setNavPage(st, model);
            
            model.addAttribute("sensorType", st);
            return "sensorTypes/view";
        }catch(Exception e){
            model.addAttribute("error", getStackTrace(e));
            return "errors/503";
        }
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Long id,Model model){
        setNavPage(null, model);
        try{
            if(!checkIfSensorTypeExist(id)){return "errors/404";}
            SensorType st = sensorTypeRepository.getReferenceById(id);
            setNavPage(st, model);
            
            model.addAttribute("sensorType", st);
            return "sensorTypes/view";
        }catch(Exception e){
            model.addAttribute("error", getStackTrace(e));
            return "errors/503";
        }
    }
    
    @PostMapping("/{id}/save")
    public String save(@PathVariable Long id, @ModelAttribute SensorType sensorType, Model model){
        setNavPage(sensorType, model);
        try{
            if(!checkIfSensorTypeExist(id)){return "errors/404";}
            sensorType.setId(id);
            sensorTypeRepository.save(sensorType);
            return "redirect:/sensorTypes/"+id;
        }catch(Exception e){
            model.addAttribute("error", getStackTrace(e));
            return "errors/503";
        }
    }

    @PostMapping("/save")
    public String create(@ModelAttribute SensorType sensorType, Model model){
        setNavPage(null, model);
        try{
            sensorTypeRepository.save(sensorType);
            return "redirect:/sensorTypes";
        }catch(Exception e){
            model.addAttribute("error", getStackTrace(e));
            return "errors/503";
        }
    }

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id, Model model){
        setNavPage(null, model);
        try{
            if(!checkIfSensorTypeExist(id)){return "errors/404";}
            sensorTypeRepository.delete(sensorTypeRepository.getReferenceById(id));
            return "redirect:/sensorTypes";
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
