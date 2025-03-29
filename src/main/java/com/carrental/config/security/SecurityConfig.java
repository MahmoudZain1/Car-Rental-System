package com.carrental.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    @Order(1)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        String[] publicUrls = {
                "/Register/**", "/Verify/**", "/ShowVerify/**", "/Login/**", "/Book/**",
                "/Profile/**", "/CarsRepository/**", "/cars/**", "/api/cars/**",
                "/css/**", "/images/**", "/static/**", "/webjars/**", "/public/**"
        };

        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(publicUrls).permitAll()
                        .requestMatchers("/Users/" ).hasRole("OWNER")
                        .requestMatchers("/home/").hasAnyRole("OWNER", "ADMIN", "USER")
                        .requestMatchers("/public/").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(login -> login
                        .loginPage("/Login")
                        .loginProcessingUrl("/login")
                        .successHandler(new AuthenticationSuccessHandler())
                        .failureUrl("/Login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/Login?logout")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .permitAll()
                )
                .sessionManagement(session -> session
                        .maximumSessions(1)
                        .expiredUrl("/Login?expired=true")

                )
                .csrf(csrf -> csrf.disable());

        return http.build();
    }




}
