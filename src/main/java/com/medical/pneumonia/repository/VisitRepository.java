package com.medical.pneumonia.repository;

import com.medical.pneumonia.entity.Visit;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VisitRepository extends JpaRepository<Visit, String> {
  List<Visit> findByPatientIdOrderByVisitDateDesc(String patientId);
}
