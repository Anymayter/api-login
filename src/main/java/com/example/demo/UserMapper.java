package com.example.demo;

import com.example.demo.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {
    @Insert("INSERT INTO Users(username, password, role_id, department_id, status) " +
            "VALUES(#{username}, #{password}, #{roleId}, #{departmentId}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "userId") // Thêm dòng này để lấy userId sau khi insert
    void insertUser(User user);

    @Select("SELECT * FROM Users WHERE username = #{username}")
    User findByUsername(String username);

    @Select("SELECT * FROM Users WHERE email = #{email}")
    @Options(useGeneratedKeys = true, keyProperty = "userId")
    User findByEmail(String email);
}
