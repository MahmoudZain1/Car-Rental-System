package com.carrental.service;

import com.carrental.entity.Users;
import com.carrental.repository.UserProfileRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoginService {

    private final UserProfileRepository userProfiles;
    private final PasswordEncoder passwordEncoder;

    public LoginService(UserProfileRepository userProfiles, PasswordEncoder passwordEncoder) {
        this.userProfiles = userProfiles;
        this.passwordEncoder = passwordEncoder;
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
