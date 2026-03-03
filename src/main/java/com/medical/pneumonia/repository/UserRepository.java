package com.medical.pneumonia.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.medical.pneumonia.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, String>{
    List<User> findAll();
    User findByUsername(String username);
    
    boolean existsByUsername(String username);
}
