package com.carrental.service;

import com.carrental.entity.Users;
import com.carrental.repository.UserProfileRepository;
import com.carrental.repository.UserRoleRepository;
import com.carrental.entity.Role;
import com.carrental.entity.enums.UserRole;
import jakarta.servlet.http.HttpSession;
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

    public UserProfileService(UserProfileRepository userProfiles, PasswordEncoder passwordEncoder, MailSenderService senderMail, UserRoleRepository userRole) {
        this.userProfiles = userProfiles;
        this.passwordEncoder = passwordEncoder;
        this.senderMail = senderMail;
        this.userRole = userRole;
    }

    @Transactional
    public void saveUser(Users user) {
        if (userProfiles.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalStateException("Email already exists!");
        }

        Users newUser = new Users();
        newUser.setFirstName(user.getFirstName());
        newUser.setLastName(user.getLastName());
        newUser.setEmail(user.getEmail());
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setIs_verifiy(false);

        Role role = user.getEmail().equals("zainmahmoud619@gmail.com") ?
                userRole.findByName(UserRole.OWNER)
                        .orElseThrow(() -> new RuntimeException("Role OWNER not found in database")) :
                userRole.findByName(UserRole.USER)
                        .orElseThrow(() -> new RuntimeException("Role USER not found in database"));

        newUser.setRoles(Collections.singleton(role));

        String code = senderMail.generateCode();
        newUser.setVerifiyCode(code);
        userProfiles.save(newUser);

        senderMail.sendEmail(user.getEmail(), "Verification Code:", code);
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
        Optional<Users> optionalUser = userProfiles.findByEmail(email);

        if (optionalUser.isPresent()) {
            Users user = optionalUser.get();

            if (!user.getIs_verifiy()) {
                throw new IllegalStateException("Account not verified! Please check your email.");
            }

            if (passwordEncoder.matches(password, user.getPassword())) {

                session.setAttribute("loggedInUser", user);
                return true;
            }
        }
        return false;
    }
}
