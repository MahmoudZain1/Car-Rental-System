package com.carrental;

import com.carrental.dto.UsersDTO;
import com.carrental.dto.mappers.UsersMapper;
import com.carrental.entity.Role;
import com.carrental.entity.Users;
import com.carrental.entity.enums.UserRole;
import com.carrental.repository.UserProfileRepository;
import com.carrental.repository.UserRoleRepository;
import com.carrental.service.LoginService;
import com.carrental.service.MailSenderService;
import com.carrental.service.UserProfileService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserProfileServiceTest {

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private MailSenderService mailSenderService;

    @Mock
    private UserRoleRepository userRoleRepository;

    @Mock
    private UsersMapper usersMapper;

    @Mock
    private LoginService loginService;

    @Mock
    private HttpSession httpSession;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserProfileService userProfileService;

    private UsersDTO usersDTO;
    private Users user;
    private Role userRole;
    private Role ownerRole;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        usersDTO = new UsersDTO();
        usersDTO.setEmail("test@example.com");
        usersDTO.setPassword("password123");

        user = new Users();
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
        user.setCreatedAt(LocalDateTime.now());
        user.setIs_verifiy(false);

        userRole = new Role();
        userRole.setName(UserRole.USER);

        ownerRole = new Role();
        ownerRole.setName(UserRole.OWNER);
    }

    @Test
    void testSaveUserSuccess() {
        when(userProfileRepository.findByEmail(usersDTO.getEmail())).thenReturn(Optional.empty());
        when(usersMapper.toEntity(usersDTO)).thenReturn(user);
        when(passwordEncoder.encode(usersDTO.getPassword())).thenReturn("encodedPassword");
        when(userRoleRepository.findByName(UserRole.USER)).thenReturn(Optional.of(userRole));
        when(mailSenderService.generateCode()).thenReturn("123456");
        when(userProfileRepository.save(any(Users.class))).thenReturn(user);

        userProfileService.saveUser(usersDTO);

        verify(userProfileRepository, times(1)).save(user);
        verify(mailSenderService, times(1)).sendEmail(usersDTO.getEmail(), "Verification Code:", "123456");
        assertEquals("encodedPassword", user.getPassword());
        assertEquals("123456", user.getVerifiyCode());
        assertFalse(user.getIs_verifiy());
        assertEquals(Collections.singleton(userRole), user.getRoles());
    }

    @Test
    void testSaveUserOwnerSuccess() {

        usersDTO.setEmail("zainmahmoud619@gmail.com");
        user.setEmail("zainmahmoud619@gmail.com");
        when(userProfileRepository.findByEmail(usersDTO.getEmail())).thenReturn(Optional.empty());
        when(usersMapper.toEntity(usersDTO)).thenReturn(user);
        when(passwordEncoder.encode(usersDTO.getPassword())).thenReturn("encodedPassword");
        when(userRoleRepository.findByName(UserRole.OWNER)).thenReturn(Optional.of(ownerRole));
        when(mailSenderService.generateCode()).thenReturn("123456");
        when(userProfileRepository.save(any(Users.class))).thenReturn(user);

        userProfileService.saveUser(usersDTO);

        verify(userProfileRepository, times(1)).save(user);
        verify(mailSenderService, times(1)).sendEmail(usersDTO.getEmail(), "Verification Code:", "123456");
        assertEquals(Collections.singleton(ownerRole), user.getRoles());
    }

    @Test
    void testSaveUserEmailAlreadyExists() {

        when(userProfileRepository.findByEmail(usersDTO.getEmail())).thenReturn(Optional.of(user));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            userProfileService.saveUser(usersDTO);
        });
        assertEquals("Email already exists!", exception.getMessage());
        verify(userProfileRepository, never()).save(any(Users.class));
        verify(mailSenderService, never()).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void testVerifyUserCodeSuccess() {

        user.setVerifiyCode("123456");
        when(userProfileRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(userProfileRepository.save(any(Users.class))).thenReturn(user);

        boolean result = userProfileService.verifyUserCode(user.getEmail(), "123456");


        assertTrue(result);
        assertTrue(user.getIs_verifiy());
        verify(userProfileRepository, times(1)).save(user);
    }

    @Test
    void testVerifyUserCodeInvalidCode() {
        user.setVerifiyCode("123456");
        when(userProfileRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        boolean result = userProfileService.verifyUserCode(user.getEmail(), "wrongCode");

        assertFalse(result);
        assertFalse(user.getIs_verifiy());
        verify(userProfileRepository, never()).save(any(Users.class));
    }

    @Test
    void testVerifyUserCodeUserNotFound() {
        when(userProfileRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());

        boolean result = userProfileService.verifyUserCode("test@example.com", "123456");

        assertFalse(result);
        verify(userProfileRepository, never()).save(any(Users.class));
    }

    @Test
    void testVerifyLoginSuccess() {

        when(loginService.verifyLogin("test@example.com", "password123", httpSession)).thenReturn(true);


        boolean result = userProfileService.verifyLogin("test@example.com", "password123", httpSession);

        assertTrue(result);
        verify(loginService, times(1)).verifyLogin("test@example.com", "password123", httpSession);
    }

    @Test
    void testVerifyLoginFailure() {

        when(loginService.verifyLogin("test@example.com", "wrongPassword", httpSession)).thenReturn(false);


        boolean result = userProfileService.verifyLogin("test@example.com", "wrongPassword", httpSession);

        assertFalse(result);
        verify(loginService, times(1)).verifyLogin("test@example.com", "wrongPassword", httpSession);
    }

    @Test
    void testGetCurrentUserSuccess() {

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test@example.com");
        when(userProfileRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(usersMapper.toDTO(user)).thenReturn(usersDTO);
        SecurityContextHolder.setContext(securityContext);


        UsersDTO result = userProfileService.GetCurrentUsr();


        assertEquals(usersDTO, result);
        verify(userProfileRepository, times(1)).findByEmail("test@example.com");
        verify(usersMapper, times(1)).toDTO(user);
    }

    @Test
    void testGetCurrentUserNotFound() {

        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("test@example.com");
        when(userProfileRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        SecurityContextHolder.setContext(securityContext);


        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            userProfileService.GetCurrentUsr();
        });
        assertEquals("User not Found", exception.getMessage());
        verify(userProfileRepository, times(1)).findByEmail("test@example.com");
        verify(usersMapper, never()).toDTO(any(Users.class));
    }
}