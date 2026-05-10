// dtos/AppUserDTO.java
package com.example.ebanking_backend.dtos;

import lombok.Data;
import java.util.List;

@Data
public class AppUserDTO {
    private String userId;
    private String username;
    private String email;
    private List<String> roles;
}