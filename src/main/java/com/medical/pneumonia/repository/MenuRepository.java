package com.medical.pneumonia.repository;

import com.medical.pneumonia.entity.Menu;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {
  List<Menu> findAllByOrderBySortOrderAsc();
}
