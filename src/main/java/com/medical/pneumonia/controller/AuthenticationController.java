package com.medical.pneumonia.controller;

import java.text.ParseException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.medical.pneumonia.dto.request.ApiResponse;
import com.medical.pneumonia.dto.request.AuthenticationRequest;
import com.medical.pneumonia.dto.request.IntrospectRequest;
import com.medical.pneumonia.dto.response.AuthenticationResponse;
import com.medical.pneumonia.dto.response.IntrospectResponse;
import com.medical.pneumonia.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;
    @PostMapping("/login")
    ApiResponse<AuthenticationResponse> authenticated(@RequestBody AuthenticationRequest request){
        return ApiResponse.<AuthenticationResponse>builder()
        .result(authenticationService.Authenticated(request))
        .build();
    }

    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest request) throws ParseException, JOSEException{
        return ApiResponse.<IntrospectResponse>builder()
        .result(authenticationService.introspect(request))
        .build();
    }
}
