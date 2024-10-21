package com.example.demo.dto;

import com.example.demo.entity.User;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class UserResponseDTO {
    private int userId;
    private String username;
    private String email;
    private int roleId;
    private int departmentId;
    private boolean status;
    private String encodedPassword;  // Thêm trường này
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public static UserResponseDTO fromUser(User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setUserId(user.getUserId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setRoleId(user.getRoleId());
        dto.setDepartmentId(user.getDepartmentId());
        dto.setStatus(user.isStatus());
        dto.setEncodedPassword(user.getPassword()); // Thêm dòng này
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }
}
