package com.example.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import com.example.exception.ObjectNotFoundException;
import com.example.model.User;
import com.example.repository.UserRepository;
import java.util.Optional;

@Component
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String mail) throws ObjectNotFoundException {
        Optional<User> user = userRepository.getUserByMail(mail);
        if (user.isPresent()){
            return user.get();
        }
        else
            throw new ObjectNotFoundException("User not found");
    }
}
