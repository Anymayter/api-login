package com.example.demo.service;


import com.example.demo.CommonCodeMapper;
import com.example.demo.dto.CommonCodeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommonCodeService {
    private final CommonCodeMapper commonCodeMapper;

    @Autowired
    public CommonCodeService(CommonCodeMapper commonCodeMapper) {
        this.commonCodeMapper = commonCodeMapper;
    }

    public List<CommonCodeDTO> getCommonCodesByGroup(String group) {
        return commonCodeMapper.findByGroup(group);
    }
}
