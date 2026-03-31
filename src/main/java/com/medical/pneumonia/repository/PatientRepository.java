package com.medical.pneumonia.repository;

import com.medical.pneumonia.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientRepository extends JpaRepository<Patient, String> {
  boolean existsByCode(String code);
}
