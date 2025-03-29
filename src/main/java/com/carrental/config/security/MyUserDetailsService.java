package com.carrental.config.security;

import com.carrental.entity.Users;
import com.carrental.repository.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserProfileRepository userProfiles;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Users users = userProfiles.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Users not found with email: " + email));

        return new MyUserDetails(users);
    }

}
