package com.medical.pneumonia.dto.request;

import lombok.Data;

@Data
public class UserCreationRequest {
    private String username;
    private String password;
}
