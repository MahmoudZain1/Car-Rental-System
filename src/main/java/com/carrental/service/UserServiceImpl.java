package com.carrental.service;

import com.carrental.repository.UserRepository;
import com.carrental.entity.PersonalInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String saveUser(PersonalInformation user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            return "Email already exists";
        }

        userRepository.save(user);
        return "Done";
    }

    public List<PersonalInformation> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<PersonalInformation> GetById(Long id) {
        return userRepository.findById(id);
    }

    public Page<PersonalInformation> searchUsers(String name,   int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return userRepository.findAllBySearch(name,  pageable);
    }

    public List<PersonalInformation> searchUsersWithoutPagination(String name) {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE);
        Page<PersonalInformation> result = userRepository.findAllBySearch(name, pageable);
        return result.getContent();
    }
}