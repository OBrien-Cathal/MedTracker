package com.cathalob.medtracker.controller.api;

import com.cathalob.medtracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminControllerApi {
    private final UserService userService;


}
