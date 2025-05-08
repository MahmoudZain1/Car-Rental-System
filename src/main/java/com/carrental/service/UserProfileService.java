package com.carrental.service;

import com.carrental.dto.UsersDTO;
import com.carrental.dto.mappers.UsersMapper;
import com.carrental.entity.Users;
import com.carrental.repository.UserProfileRepository;
import com.carrental.repository.UserRoleRepository;
import com.carrental.entity.Role;
import com.carrental.entity.enums.UserRole;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

@Service
public class UserProfileService {

    private final UserProfileRepository userProfiles;
    private final PasswordEncoder passwordEncoder;
    private final MailSenderService senderMail;
    private final UserRoleRepository userRole;
    private final UsersMapper usersMapper;
    private static final String OWNER_EMAIL = "zainmahmoud619@gmail.com";
    private final LoginService loginService;


    public UserProfileService(UserProfileRepository userProfiles, PasswordEncoder passwordEncoder,
                              MailSenderService senderMail, UserRoleRepository userRole,
                              UsersMapper usersMapper, LoginService loginService) {
        this.userProfiles = userProfiles;
        this.passwordEncoder = passwordEncoder;
        this.senderMail = senderMail;
        this.userRole = userRole;
        this.usersMapper = usersMapper;
        this.loginService = loginService;
    }

    @Transactional
    public void saveUser(UsersDTO usersDTO) {
        if (userProfiles.findByEmail(usersDTO.getEmail()).isPresent()) {
            throw new IllegalStateException("Email already exists!");
        }

        Users newUser = usersMapper.toEntity(usersDTO);
        newUser.setPassword(passwordEncoder.encode(usersDTO.getPassword()));
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setIs_verifiy(false);

        Role role = usersDTO.getEmail().equals(OWNER_EMAIL) ?
                userRole.findByName(UserRole.OWNER)
                        .orElseThrow(() -> new RuntimeException("Role OWNER not found in database")) :
                userRole.findByName(UserRole.USER)
                        .orElseThrow(() -> new RuntimeException("Role USER not found in database"));

        newUser.setRoles(Collections.singleton(role));

        String code = senderMail.generateCode();
        newUser.setVerifiyCode(code);
        userProfiles.save(newUser);

        senderMail.sendEmail(usersDTO.getEmail(), "Verification Code:", code);
    }

    @Transactional
    public boolean verifyUserCode(String email, String code) {
        Optional<Users> optionalUser = userProfiles.findByEmail(email);
        if (optionalUser.isPresent()) {
            Users user = optionalUser.get();
            if (user.getVerifiyCode().equals(code)) {
                user.setIs_verifiy(true);
                userProfiles.save(user);
                return true;
            }
        }
        return false;
    }

    public boolean verifyLogin(String email, String password, HttpSession session) {
        return loginService.verifyLogin(email, password, session);
    }

   public UsersDTO GetCurrentUsr(){
        String Email  =  SecurityContextHolder.getContext().getAuthentication().getName();
        Users user = userProfiles.findByEmail(Email)
                .orElseThrow(() -> new UsernameNotFoundException("User not Found"));

        return usersMapper.toDTO(user);
   }
}
