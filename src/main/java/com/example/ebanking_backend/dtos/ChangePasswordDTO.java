// dtos/ChangePasswordDTO.java
package com.example.ebanking_backend.dtos;

import lombok.Data;

@Data
public class ChangePasswordDTO {
    private String oldPassword;
    private String newPassword;
    private String confirmPassword;
}