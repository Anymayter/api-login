package com.example.demo.controller;

import com.example.demo.dto.CommonCodeDTO;
import com.example.demo.service.CommonCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CommonCodeController {
    private final CommonCodeService commonCodeService;

    @Autowired
    public CommonCodeController(CommonCodeService commonCodeService) {
        this.commonCodeService = commonCodeService;
    }

    @GetMapping("/api/common-codes")
    public List<CommonCodeDTO> getCommonCodes(@RequestParam String group) {
        return commonCodeService.getCommonCodesByGroup(group);
    }
}