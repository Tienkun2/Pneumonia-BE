package com.medical.pneumonia.repository;

import com.medical.pneumonia.entity.MedicalImage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicalImageRepository extends JpaRepository<MedicalImage, String> {
  List<MedicalImage> findByVisitId(String visitId);

  List<MedicalImage> findAllByVisitIdIn(List<String> visitIds);
}
