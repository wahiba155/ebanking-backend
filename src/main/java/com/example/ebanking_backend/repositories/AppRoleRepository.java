// repositories/AppRoleRepository.java
package com.example.ebanking_backend.repositories;

import com.example.ebanking_backend.entities.AppRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppRoleRepository extends JpaRepository<AppRole, String> {
}