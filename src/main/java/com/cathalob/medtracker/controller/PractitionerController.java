package com.cathalob.medtracker.controller;

import com.cathalob.medtracker.service.impl.UserServiceImpl;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PractitionerController {
    private final UserServiceImpl userService;

    public PractitionerController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @GetMapping("/practitioner/patients")
    public String getPatients(Model model, Authentication authentication) {
        model.addAttribute("users", userService.getPatientUserModels(authentication.getName()));
        return "practitioner/patientsList";
    }


}
