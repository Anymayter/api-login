package com.example.demo.controller;

import com.example.demo.component.JwtUtil;
import com.example.demo.dto.UserRegistrationDTO;
import com.example.demo.dto.UserResponseDTO;
import com.example.demo.entity.AuthenticationRequest;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.mybatis.logging.Logger;
import org.mybatis.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RequestMapping
@RestController
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegistrationDTO registrationDTO) {
        try {
            User user = new User();
            user.setUsername(registrationDTO.getUsername());
            user.setPassword(registrationDTO.getPassword());
            user.setEmail(registrationDTO.getEmail());
            user.setRoleId(registrationDTO.getRoleId());
            user.setDepartmentId(registrationDTO.getDepartmentId());
            user.prepareForRegistration();

            User registeredUser = userService.registerUser(user);

            return ResponseEntity.ok(UserResponseDTO.fromUser(registeredUser));

        } catch (RuntimeException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new ErrorResponse(e.getMessage()));
        }
    }

//    @PostMapping("/login")
//    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
//        try {
//            authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUsernameOrEmail(), authenticationRequest.getPassword())
//            );
//        } catch (BadCredentialsException e) {
//            throw new Exception("Incorrect username or password", e);
//        }
//
//        // Lấy thông tin người dùng từ userService
//        final UserDetails userDetails = userService.loadUserByUsername(authenticationRequest.getUsernameOrEmail());
//
//        // Tạo JWT token
//        final String jwt = jwtUtil.generateToken(userDetails);
//
//        // Trả về thông tin người dùng và JWT
//        User user = userService.getUserByUsernameOrEmail(authenticationRequest.getUsernameOrEmail());
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("token", jwt);
//
//        return ResponseEntity.ok(response);
//    }

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authenticationRequest.getUsernameOrEmail(), authenticationRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new Exception("Incorrect username or password", e);
        }

        // Lấy thông tin người dùng từ userService
        final UserDetails userDetails = userService.loadUserByUsername(authenticationRequest.getUsernameOrEmail());

        // Tạo JWT token
        final String jwt = jwtUtil.generateToken(userDetails);

        // Trả về thông tin người dùng và JWT
        User user = userService.getUserByUsernameOrEmail(authenticationRequest.getUsernameOrEmail());

        Map<String, Object> response = new HashMap<>();
        response.put("token", jwt);
        response.put("user", UserResponseDTO.fromUser(user)); // Thêm thông tin người dùng vào response

        return ResponseEntity.ok(response);
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserInfo(@RequestHeader("Authorization") String token) {
        try {
            // Lấy JWT từ header Authorization
            String jwt = token.substring(7); // Bỏ chữ "Bearer " khỏi token

            // Trích xuất username từ JWT
            String username = jwtUtil.extractUsername(jwt);

            // Lấy thông tin người dùng từ username
            UserDetails userDetails = userService.loadUserByUsername(username);

            // Kiểm tra tính hợp lệ của JWT
            if (username != null && jwtUtil.validateToken(jwt, userDetails)) {
                User user = userService.getUserByUsernameOrEmail(username);

                Map<String, Object> response = new HashMap<>();
                response.put("user", user); // Thông tin người dùng đầy đủ

                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(401).body("Invalid JWT Token");
            }
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).body(e.getMessage());
        }
    }

    @Data
    @AllArgsConstructor
    class ErrorResponse {
        private String message;
    }
}