package com.medical.pneumonia.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.medical.pneumonia.dto.request.UserCreationRequest;
import com.medical.pneumonia.dto.response.UserCreationResponse;
import com.medical.pneumonia.entity.Users;
import com.medical.pneumonia.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }   
    
    @GetMapping("/list")
    public ResponseEntity<List<Users>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PostMapping()
    public ResponseEntity<UserCreationResponse> createUser(@RequestBody UserCreationRequest userCreationRequest) {
        return ResponseEntity.ok(userService.createUser(userCreationRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserCreationResponse> getUserById(@PathVariable String id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserCreationResponse> updateUser(@PathVariable String id, @RequestBody UserCreationRequest userCreationRequest) {
        return ResponseEntity.ok(userService.updateUser(id, userCreationRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UserCreationResponse> deleteUser(@PathVariable String id) {
        return ResponseEntity.ok(userService.deleteUser(id));
    }       
}
