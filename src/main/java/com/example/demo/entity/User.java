package com.example.demo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import jakarta.validation.constraints.*;

import java.sql.Timestamp;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private int userId;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9._-]{3,}$", message = "Username can only contain letters, numbers, dots, underscores and hyphens")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters long")
    private String password;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @Min(value = 1, message = "Invalid role ID")
    private int roleId;

    @Min(value = 1, message = "Invalid department ID")
    private int departmentId;

    @Builder.Default
    private boolean status = true;

    @Builder.Default
    private Timestamp createdAt = Timestamp.from(Instant.now());

    private Timestamp updatedAt;

    // Custom constructor for registration
    public static User createNewUser(String username, String password, String email, int roleId, int departmentId) {
        return User.builder()
                .username(username)
                .password(password)
                .email(email)
                .roleId(roleId)
                .departmentId(departmentId)
                .status(true)
                .createdAt(Timestamp.from(Instant.now()))
                .build();
    }

    // Method to update timestamp before save
    public void setUpdatedTimestamp() {
        this.updatedAt = Timestamp.from(Instant.now());
    }

    // Method to prepare user for registration
    public void prepareForRegistration() {
        this.status = true;
        this.createdAt = Timestamp.from(Instant.now());
        this.updatedAt = this.createdAt;
    }

    // ToString method that excludes password
    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", password='" + password +
                ", email='" + email + '\'' +
                ", roleId=" + roleId +
                ", departmentId=" + departmentId +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}