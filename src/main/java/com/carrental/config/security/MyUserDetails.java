package com.carrental.config.security;

import com.carrental.entity.Users;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.stream.Collectors;

public class MyUserDetails implements UserDetails , CredentialsContainer {

    public MyUserDetails(Users users) {
        this.users = users;
    }

    private Users users;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return users.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_"+role.getName().name()))
                .collect(Collectors.toSet());
    }
    @Override
    public String getPassword() {
        return users.getPassword();
    }

    @Override
    public String getUsername() {
        return users.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return users.getIs_verifiy() !=null && users.getIs_verifiy();
    }

    @Override
    public void eraseCredentials() {
       users.setPassword(null);
    }
}


