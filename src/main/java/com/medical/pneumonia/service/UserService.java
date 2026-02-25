package com.medical.pneumonia.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.medical.pneumonia.repository.UserRepository;
import com.medical.pneumonia.dto.request.UserCreationRequest;
import com.medical.pneumonia.dto.response.UserCreationResponse;
import com.medical.pneumonia.entity.Users;

@Service
public class UserService {
    private final UserRepository userRepository;
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }    
    public UserCreationResponse createUser(UserCreationRequest userCreationRequest) {
        Users user = new Users();
        userRepository.save(user);
        return UserCreationResponse.builder()
                .username(userCreationRequest.getUsername())
                .password(userCreationRequest.getPassword())
                .build();
    }
    public List<Users> getAllUsers() {
        return userRepository.findAll();
    }

    public UserCreationResponse getUserById(String id) {
        Users user = userRepository.findById(id).orElse(null);
        return UserCreationResponse.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .build();
    }

    public UserCreationResponse updateUser(String id, UserCreationRequest userCreationRequest) {
        Users user = userRepository.findById(id).orElse(null);
        user.setUsername(userCreationRequest.getUsername());
        user.setPassword(userCreationRequest.getPassword());
        userRepository.save(user);
        return UserCreationResponse.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .build();
    }   

    public UserCreationResponse deleteUser(String id) {
        Users user = userRepository.findById(id).orElse(null);
        userRepository.delete(user);
        return UserCreationResponse.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .build();
    }  
}
