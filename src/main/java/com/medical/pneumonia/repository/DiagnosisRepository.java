package com.medical.pneumonia.repository;

import com.medical.pneumonia.entity.Diagnosis;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DiagnosisRepository extends JpaRepository<Diagnosis, String> {
  List<Diagnosis> findByVisitId(String visitId);

  List<Diagnosis> findAllByVisitIdIn(List<String> visitIds);

  @Query("SELECT d.result, COUNT(d) FROM Diagnosis d GROUP BY d.result")
  List<Object[]> countDiagnosesByResult();
}
