package com.medical.pneumonia.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.medical.pneumonia.entity.Permission;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, String>{
    
}
