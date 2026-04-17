package com.medical.pneumonia.repository;

import com.medical.pneumonia.entity.Visit;
import java.time.Instant;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VisitRepository extends JpaRepository<Visit, String> {
  @EntityGraph(attributePaths = {"patient"})
  Page<Visit> findAll(Pageable pageable);

  @EntityGraph(attributePaths = {"patient"})
  List<Visit> findByPatientIdOrderByVisitDateDesc(String patientId);

  long countByVisitDateBetween(Instant start, Instant end);

  @EntityGraph(attributePaths = {"patient"})
  List<Visit> findByVisitDateAfter(Instant date);

  @EntityGraph(attributePaths = {"patient"})
  Page<Visit> findByOrderByVisitDateDesc(Pageable pageable);

  @Query(
      value =
          "SELECT TO_CHAR(v.visit_date, 'YYYY-MM-DD') as date, COUNT(*) as count "
              + "FROM visits v WHERE v.visit_date >= :startDate "
              + "GROUP BY TO_CHAR(v.visit_date, 'YYYY-MM-DD')",
      nativeQuery = true)
  List<Object[]> countVisitsByDateNative(@Param("startDate") Instant startDate);
}
