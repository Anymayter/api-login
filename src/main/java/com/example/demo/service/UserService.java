package com.example.demo.service;

import com.example.demo.UserMapper;
import com.example.demo.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@Transactional
public class UserService implements UserDetailsService {
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserMapper userMapper, @Lazy BCryptPasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User registerUser(User user) {
        try {


            // Validate user input
            validateUserInput(user);

            // Check if username already exists
            if (findByUsername(user.getUsername()) != null) {
                throw new RuntimeException("Username already exists");
            }


            // Encode password
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);

            // Set default values
            user.setStatus(true);

            // Insert user
            userMapper.insertUser(user);

            log.info("User registered successfully with Id: {}", user.getUserId());

            // Return the user with encoded password
            return user;
        } catch (Exception e) {
            log.error("Error registering user: {}", e.getMessage());
            throw new RuntimeException("Error registering user", e);
        }
    }

    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        try {
            return userMapper.findByUsername(username);
        } catch (Exception e) {
            log.error("Error finding user by username: {}", e.getMessage());
            throw new RuntimeException("Error finding user", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            User user = findByUsername(username);
            if (user == null) {
                log.warn("User not found: {}", username);
                throw new UsernameNotFoundException("User not found with username: " + username);
            }

            // Check if user is active
            if (!user.isStatus()) {
                log.warn("User account is disabled: {}", username);
                throw new UsernameNotFoundException("User account is disabled");
            }

            // Create authorities based on user's role
            List<SimpleGrantedAuthority> authorities = getAuthorities(user);

            return new org.springframework.security.core.userdetails.User(
                    user.getUsername(),
                    user.getPassword(),
                    user.isStatus(), // enabled
                    true, // accountNonExpired
                    true, // credentialsNonExpired
                    true, // accountNonLocked
                    authorities
            );
        } catch (UsernameNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error loading user by username: {}", e.getMessage());
            throw new RuntimeException("Error loading user", e);
        }
    }

    private List<SimpleGrantedAuthority> getAuthorities(User user) {
        // Add role-based authority
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));

        // If you want to add more roles based on roleId:
        /*
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        switch (user.getRoleId()) {
            case 1:
                authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                break;
            case 2:
                authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                break;
            // Add more roles as needed
        }
        return authorities;
        */
    }

    @Transactional(readOnly = true)
    public User getUserByUsernameOrEmail(String usernameOrEmail) {
        try {
            User user = userMapper.findByUsername(usernameOrEmail);
            if (user == null) {
                user = userMapper.findByEmail(usernameOrEmail);
            }
            return user;
        } catch (Exception e) {
            log.error("Error finding user by username or email: {}", e.getMessage());
            throw new RuntimeException("Error finding user", e);
        }
    }

    private void validateUserInput(User user) {
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        if (user.getEmail() == null || !user.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }
}