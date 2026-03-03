package com.medical.pneumonia.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized exception"),
    INVALID_KEY(9998, "Invalid key"),
    //User
    USER_EXISTED(1001, "User already exists"),
    USER_NOT_FOUND(1002, "User not found"),
    USERNAME_INVALID(1101, "Username must be at least 6 characters long"),
    PASSWORD_INVALID(1102, "Password must be at least 6 characters long"),
    ;

    ErrorCode(int code, String message){
        this.code = code;
        this.message = message;
    }

    private int code;
    private String message;

}
