package com.example.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.model.User;
import com.example.repository.UserRepository;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UserService {

    public final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> getUserById(int id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByMail(String mail) {
        return userRepository.getUserByMail(mail);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.getUserByUsername(username);
    }

    @Transactional
    public void createUser(User user) {
        userRepository.save(user);
    }
}
