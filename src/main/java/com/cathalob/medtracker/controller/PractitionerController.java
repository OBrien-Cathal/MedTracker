package com.cathalob.medtracker.controller;

import com.cathalob.medtracker.service.impl.UserServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@AllArgsConstructor
public class PractitionerController {
    private final UserServiceImpl userService;


}
