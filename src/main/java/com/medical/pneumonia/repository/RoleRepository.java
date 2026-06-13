package com.medical.pneumonia.repository;

import com.medical.pneumonia.entity.Role;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {
  @Override
  @EntityGraph(attributePaths = {"permissions"})
  List<Role> findAll();

  @Query("SELECT r FROM Role r LEFT JOIN FETCH r.permissions WHERE r.name = :name")
  java.util.Optional<Role> findByNameWithPermissions(@Param("name") String name);
}
