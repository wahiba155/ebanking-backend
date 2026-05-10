// services/AccountServiceImpl.java
package com.example.ebanking_backend.services;

import com.example.ebanking_backend.dtos.AppUserDTO;
import com.example.ebanking_backend.dtos.NewUserDTO;
import com.example.ebanking_backend.entities.AppRole;
import com.example.ebanking_backend.entities.AppUser;
import com.example.ebanking_backend.repositories.AppRoleRepository;
import com.example.ebanking_backend.repositories.AppUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class AccountServiceImpl implements AccountService {

    private AppUserRepository appUserRepository;
    private AppRoleRepository appRoleRepository;
    private PasswordEncoder passwordEncoder;

    @Override
    public AppUser addNewUser(NewUserDTO newUserDTO) {
        AppUser appUser = appUserRepository.findByUsername(
                newUserDTO.getUsername());
        if (appUser != null)
            throw new RuntimeException("User already exists");

        AppUser user = new AppUser();
        user.setUserId(UUID.randomUUID().toString());
        user.setUsername(newUserDTO.getUsername());
        user.setEmail(newUserDTO.getEmail());
        user.setPassword(passwordEncoder.encode(newUserDTO.getPassword()));
        user.setRoles(new ArrayList<>());

        // Ajouter les rôles
        if (newUserDTO.getRoles() != null) {
            newUserDTO.getRoles().forEach(roleName -> {
                AppRole role = appRoleRepository.findById(roleName)
                        .orElseThrow(() -> new RuntimeException(
                                "Role not found: " + roleName));
                user.getRoles().add(role);
            });
        }
        return appUserRepository.save(user);
    }

    @Override
    public AppRole addNewRole(String roleName) {
        AppRole role = new AppRole();
        role.setRoleName(roleName);
        return appRoleRepository.save(role);
    }

    @Override
    public void addRoleToUser(String username, String roleName) {
        AppUser user = appUserRepository.findByUsername(username);
        if (user == null)
            throw new RuntimeException("User not found");
        AppRole role = appRoleRepository.findById(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        user.getRoles().add(role);
    }

    @Override
    public void removeRoleFromUser(String username, String roleName) {
        AppUser user = appUserRepository.findByUsername(username);
        if (user == null)
            throw new RuntimeException("User not found");
        AppRole role = appRoleRepository.findById(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        user.getRoles().remove(role);
    }

    @Override
    public AppUser loadUserByUsername(String username) {
        return appUserRepository.findByUsername(username);
    }

    @Override
    public List<AppUserDTO> listUsers() {
        return appUserRepository.findAll().stream()
                .map(user -> {
                    AppUserDTO dto = new AppUserDTO();
                    dto.setUserId(user.getUserId());
                    dto.setUsername(user.getUsername());
                    dto.setEmail(user.getEmail());
                    dto.setRoles(user.getRoles().stream()
                            .map(AppRole::getRoleName)
                            .collect(Collectors.toList()));
                    return dto;
                }).collect(Collectors.toList());
    }

    @Override
    public void deleteUser(String username) {
        AppUser user = appUserRepository.findByUsername(username);
        if (user == null)
            throw new RuntimeException("User not found");
        appUserRepository.delete(user);
    }

    @Override
    public void changePassword(String username,
                               String oldPassword,
                               String newPassword) {
        AppUser user = appUserRepository.findByUsername(username);
        if (user == null)
            throw new RuntimeException("User not found");
        if (!passwordEncoder.matches(oldPassword, user.getPassword()))
            throw new RuntimeException("Old password is incorrect");
        user.setPassword(passwordEncoder.encode(newPassword));
        appUserRepository.save(user);
    }
}