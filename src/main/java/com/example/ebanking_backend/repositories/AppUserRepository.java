// repositories/AppUserRepository.java
package com.example.ebanking_backend.repositories;

import com.example.ebanking_backend.entities.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser, String> {
    AppUser findByUsername(String username);
}