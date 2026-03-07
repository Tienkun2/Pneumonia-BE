package com.medical.pneumonia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.medical.pneumonia.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {
    
}
