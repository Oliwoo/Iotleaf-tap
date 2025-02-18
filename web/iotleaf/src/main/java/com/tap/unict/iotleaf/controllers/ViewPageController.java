package com.tap.unict.iotleaf.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;



@Controller
@RequestMapping("views")
public class ViewPageController {
    @GetMapping("")
    public String home(Model model) {
        return "redirect:/devices";
    }
    
}
