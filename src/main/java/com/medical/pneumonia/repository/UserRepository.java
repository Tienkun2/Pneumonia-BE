package com.medical.pneumonia.repository;

import com.medical.pneumonia.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
  @EntityGraph(attributePaths = {"roles", "roles.permissions"})
  List<User> findAll();

  @EntityGraph(attributePaths = {"roles", "roles.permissions"})
  Page<User> findAll(Pageable pageable);

  boolean existsByUsername(String username);

  @EntityGraph(attributePaths = {"roles", "roles.permissions"})
  Optional<User> findByUsername(String username);

  Optional<User> findByActivationToken(String token);

  Optional<User> findByEmail(String email);

  boolean existsByEmail(String email);
}
