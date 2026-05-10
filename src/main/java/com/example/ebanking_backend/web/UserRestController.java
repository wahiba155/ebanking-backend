package com.example.ebanking_backend.web;

import com.example.ebanking_backend.SecurityUtils;
import com.example.ebanking_backend.dtos.AppUserDTO;
import com.example.ebanking_backend.dtos.ChangePasswordDTO;
import com.example.ebanking_backend.dtos.NewUserDTO;
import com.example.ebanking_backend.entities.AppRole;
import com.example.ebanking_backend.entities.AppUser;
import com.example.ebanking_backend.services.AccountService;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@AllArgsConstructor
@CrossOrigin("*")
@RequestMapping("/users")
public class UserRestController {

    private AccountService accountService;

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    public List<AppUserDTO> listUsers() {
        return accountService.listUsers();
    }

    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    public AppUserDTO addUser(@RequestBody NewUserDTO newUserDTO) {
        AppUser user = accountService.addNewUser(newUserDTO);
        AppUserDTO dto = new AppUserDTO();
        dto.setUserId(user.getUserId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRoles(user.getRoles().stream()
                .map(AppRole::getRoleName)
                .collect(Collectors.toList()));
        return dto;
    }

    @DeleteMapping("/{username}")
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    public void deleteUser(@PathVariable String username) {
        accountService.deleteUser(username);
    }

    @PostMapping("/addRole")
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    public void addRoleToUser(@RequestBody Map<String, String> body) {
        accountService.addRoleToUser(
                body.get("username"), body.get("roleName"));
    }

    @PostMapping("/removeRole")
    @PreAuthorize("hasAuthority('SCOPE_ROLE_ADMIN')")
    public void removeRoleFromUser(@RequestBody Map<String, String> body) {
        accountService.removeRoleFromUser(
                body.get("username"), body.get("roleName"));
    }

    @PutMapping("/changePassword")
    public void changePassword(@RequestBody ChangePasswordDTO dto) {
        String username = SecurityUtils.getCurrentUser();
        accountService.changePassword(
                username, dto.getOldPassword(), dto.getNewPassword());
    }

    @GetMapping("/profile")
    public AppUserDTO getProfile() {
        String username = SecurityUtils.getCurrentUser();
        return toDTO(accountService.loadUserByUsername(username));
    }

    private AppUserDTO toDTO(
            com.example.ebanking_backend.entities.AppUser user) {
        AppUserDTO dto = new AppUserDTO();
        dto.setUserId(user.getUserId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        return dto;
    }
}