package com.medical.pneumonia.repository;

import com.medical.pneumonia.entity.Visit;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VisitRepository extends JpaRepository<Visit, String> {
  @EntityGraph(attributePaths = {"patient"})
  Page<Visit> findAll(Pageable pageable);

  @EntityGraph(attributePaths = {"patient"})
  List<Visit> findByPatientIdOrderByVisitDateDesc(String patientId);
}
