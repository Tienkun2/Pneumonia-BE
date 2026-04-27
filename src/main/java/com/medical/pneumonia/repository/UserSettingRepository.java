package com.medical.pneumonia.repository;

import com.medical.pneumonia.entity.User;
import com.medical.pneumonia.entity.UserSetting;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSettingRepository extends JpaRepository<UserSetting, String> {
  Optional<UserSetting> findByUser(User user);
}
