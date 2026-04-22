package com.medical.pneumonia.repository;

import com.medical.pneumonia.entity.User;
import com.medical.pneumonia.entity.UserDevice;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDeviceRepository extends JpaRepository<UserDevice, String> {
  List<UserDevice> findByUserOrderByLastAccessDesc(User user);

  Optional<UserDevice> findByUserIdAndUserAgent(String userId, String userAgent);

  long countByUser(User user);
}
