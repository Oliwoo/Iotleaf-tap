package com.tap.unict.iotleaf.controllers;

import java.util.List;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.tap.unict.iotleaf.models.ActivePage;
import com.tap.unict.iotleaf.models.PathUrl;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;



@Controller
public class CustomErrorController implements ErrorController{

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());

            if(statusCode == HttpStatus.NOT_FOUND.value()) {
                model.addAttribute("activePage", new ActivePage("errors", "Page not found","error",List.of(
                    new PathUrl("404","Page not found", "/404")
                )));
                return "errors/404";
            }
            else if(statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                model.addAttribute("activePage", new ActivePage("errors", "Server error","error",List.of(
                    new PathUrl("500","Server Error", "/500")
                )));
                return "errors/500";
            }
        }
        return "error";
    }

    public String getErrorPath() {
        return "/error";
    }
    
}
