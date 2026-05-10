// services/AccountService.java
package com.example.ebanking_backend.services;

import com.example.ebanking_backend.dtos.AppUserDTO;
import com.example.ebanking_backend.dtos.NewUserDTO;
import com.example.ebanking_backend.entities.AppRole;
import com.example.ebanking_backend.entities.AppUser;

import java.util.List;

public interface AccountService {
    AppUser addNewUser(NewUserDTO newUserDTO);
    AppRole addNewRole(String roleName);
    void addRoleToUser(String username, String roleName);
    void removeRoleFromUser(String username, String roleName);
    AppUser loadUserByUsername(String username);
    List<AppUserDTO> listUsers();
    void deleteUser(String username);
    void changePassword(String username, String oldPassword,
                        String newPassword);
}