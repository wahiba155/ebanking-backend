// dtos/NewUserDTO.java
package com.example.ebanking_backend.dtos;

import lombok.Data;
import java.util.List;

@Data
public class NewUserDTO {
    private String username;
    private String password;
    private String email;
    private List<String> roles;
}