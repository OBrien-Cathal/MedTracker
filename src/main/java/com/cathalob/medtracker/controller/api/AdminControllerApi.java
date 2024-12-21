package com.cathalob.medtracker.controller.api;

import com.cathalob.medtracker.model.UserModel;

import com.cathalob.medtracker.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
public class AdminControllerApi {
    private final UserService userService;
    @GetMapping
    public ResponseEntity<List<UserModel>> getUserModels(){
        return ResponseEntity.ok(userService.getUserModels());
    }
}
