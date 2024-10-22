package com.example.demo;

import com.example.demo.dto.CommonCodeDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CommonCodeMapper {
    @Select("SELECT code, `gyroup`, name FROM common_code WHERE `group` = #{group}")
    List<CommonCodeDTO> findByGroup(String group);
}
