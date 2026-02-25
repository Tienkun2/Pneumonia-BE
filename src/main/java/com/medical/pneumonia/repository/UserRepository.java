package com.medical.pneumonia.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.medical.pneumonia.entity.Users;

@Repository
public interface UserRepository extends JpaRepository<Users, String>{
    List<Users> findAll();
    Users findByUsername(String username);
}
