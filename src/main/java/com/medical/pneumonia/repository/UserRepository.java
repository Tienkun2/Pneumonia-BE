package com.medical.pneumonia.repository;

import com.medical.pneumonia.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
  List<User> findAll();

  boolean existsByUsername(String username);

  Optional<User> findByUsername(String username);
}
