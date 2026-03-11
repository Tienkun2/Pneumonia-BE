package com.medical.pneumonia.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.medical.pneumonia.dto.request.UserCreationRequest;
import com.medical.pneumonia.dto.request.UserUpdateRequest;
import com.medical.pneumonia.dto.response.UserResponse;
import com.medical.pneumonia.entity.Role;
import com.medical.pneumonia.entity.User;
import com.medical.pneumonia.exception.AppException;
import com.medical.pneumonia.mapper.UserMapper;
import com.medical.pneumonia.repository.RoleRepository;
import com.medical.pneumonia.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    RoleRepository roleRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    UserMapper userMapper;

    @InjectMocks
    UserService userService;

    User user;
    UserResponse response;
    Role adminRole;
    UserCreationRequest creationRequest;
    UserUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {

        adminRole = Role.builder()
                .name("ADMIN")
                .build();

        user = User.builder()
                .id("1")
                .username("testuser")
                .roles(Set.of(adminRole))
                .build();

        response = UserResponse.builder()
                .username("testuser")
                .build();

        creationRequest = UserCreationRequest.builder()
                .username("testuser")
                .password("123456")
                .roles(List.of("ADMIN"))
                .build();

        updateRequest = UserUpdateRequest.builder()
                .password("newpassword")
                .roles(List.of("ADMIN"))
                .build();
    }

    // ================= CREATE USER =================

    @Test
    void createUser_success() {

        when(userRepository.existsByUsername("testuser"))
                .thenReturn(false);

        when(userMapper.toUser(creationRequest))
                .thenReturn(user);

        when(passwordEncoder.encode("123456"))
                .thenReturn("encodedPassword");

        when(roleRepository.findAllById(creationRequest.getRoles()))
                .thenReturn(List.of(adminRole));

        when(userRepository.save(user))
                .thenReturn(user);

        when(userMapper.toUserResponse(user))
                .thenReturn(response);

        UserResponse result = userService.createUser(creationRequest);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());

        verify(userRepository).save(user);
    }

    @Test
    void createUser_userAlreadyExists_throwException() {

        when(userRepository.existsByUsername("testuser"))
                .thenReturn(true);

        assertThrows(AppException.class,
                () -> userService.createUser(creationRequest));

        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_roleNotFound_throwException() {

        when(userRepository.existsByUsername("testuser"))
                .thenReturn(false);

        when(userMapper.toUser(creationRequest))
                .thenReturn(user);

        when(roleRepository.findAllById(creationRequest.getRoles()))
                .thenReturn(List.of());

        assertThrows(AppException.class,
                () -> userService.createUser(creationRequest));
    }

    @Test
        void createUser_passwordShouldBeEncoded() {

        when(userRepository.existsByUsername("testuser"))
                .thenReturn(false);

        when(userMapper.toUser(creationRequest))
                .thenReturn(user);

        when(passwordEncoder.encode("123456"))
                .thenReturn("encodedPassword");

        when(roleRepository.findAllById(creationRequest.getRoles()))
                .thenReturn(List.of(adminRole));

        when(userRepository.save(user))
                .thenReturn(user);

        when(userMapper.toUserResponse(user))
                .thenReturn(response);

        userService.createUser(creationRequest);

        verify(passwordEncoder).encode("123456");
        }

    // ================= GET USER =================

    @Test
    void getUserById_success() {

        when(userRepository.findById("1"))
                .thenReturn(Optional.of(user));

        when(userMapper.toUserResponse(user))
                .thenReturn(response);

        UserResponse result = userService.getUserById("1");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void getUserById_userNotFound_throwException() {

        when(userRepository.findById("1"))
                .thenReturn(Optional.empty());

        assertThrows(AppException.class,
                () -> userService.getUserById("1"));
    }

    @Test
void createUser_rolesShouldBeSetCorrectly() {

    when(userRepository.existsByUsername("testuser"))
            .thenReturn(false);

    when(userMapper.toUser(creationRequest))
            .thenReturn(user);

    when(passwordEncoder.encode("123456"))
            .thenReturn("encodedPassword");

    when(roleRepository.findAllById(creationRequest.getRoles()))
            .thenReturn(List.of(adminRole));

    when(userRepository.save(user))
            .thenReturn(user);

    when(userMapper.toUserResponse(user))
            .thenReturn(response);

    userService.createUser(creationRequest);

    assertTrue(user.getRoles().contains(adminRole));
}

    // ================= DELETE USER =================

    @Test
    void deleteUser_success() {

        when(userRepository.findById("1"))
                .thenReturn(Optional.of(user));

        userService.deleteUser("1");

        verify(userRepository).delete(user);
    }

    @Test
    void deleteUser_userNotFound_throwException() {

        when(userRepository.findById("1"))
                .thenReturn(Optional.empty());

        assertThrows(AppException.class,
                () -> userService.deleteUser("1"));
    }

    // ================= UPDATE USER =================

    @Test
    void updateUser_success() {

        when(userRepository.findById("1"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.encode(updateRequest.getPassword()))
                .thenReturn("encodedPassword");

        when(roleRepository.findAllById(updateRequest.getRoles()))
                .thenReturn(List.of(adminRole));

        when(userRepository.save(user))
                .thenReturn(user);

        when(userMapper.toUserResponse(user))
                .thenReturn(response);

        UserResponse result = userService.updateUser("1", updateRequest);

        assertNotNull(result);

        verify(userMapper).updateUser(user, updateRequest);
        verify(userRepository).save(user);
    }
    
    @Test
        void updateUser_rolesShouldBeUpdated() {

        when(userRepository.findById("1"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.encode(updateRequest.getPassword()))
                .thenReturn("encodedPassword");

        when(roleRepository.findAllById(updateRequest.getRoles()))
                .thenReturn(List.of(adminRole));

        when(userRepository.save(user))
                .thenReturn(user);

        when(userMapper.toUserResponse(user))
                .thenReturn(response);

        userService.updateUser("1", updateRequest);

        assertEquals(1, user.getRoles().size());
        }

    // ================= GET ALL USERS =================

    @Test
    void getAllUsers_success() {

        when(userRepository.findAll())
                .thenReturn(List.of(user));

        when(userMapper.toUserResponse(user))
                .thenReturn(response);

        List<UserResponse> result = userService.getAllUsers();

        assertEquals(1, result.size());
    }

    @Test
        void getAllUsers_mapperShouldBeCalled() {

        when(userRepository.findAll())
                .thenReturn(List.of(user));

        when(userMapper.toUserResponse(user))
                .thenReturn(response);

        userService.getAllUsers();

        verify(userMapper).toUserResponse(user);
        }
    // ================= GET MY INFO =================

    @Test
    void getMyInfo_success() {

        Authentication authentication = mock(Authentication.class);

        when(authentication.getName()).thenReturn("testuser");

        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(user));

        when(userMapper.toUserResponse(user))
                .thenReturn(response);

        UserResponse result = userService.getMyInfo();

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }

    @Test
    void getMyInfo_userNotFound_throwException() {

        Authentication authentication = mock(Authentication.class);

        when(authentication.getName()).thenReturn("testuser");

        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.empty());

        assertThrows(AppException.class,
                () -> userService.getMyInfo());
    }
}