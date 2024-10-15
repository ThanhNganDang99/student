package com.example.student.service;

import com.example.student.dto.request.RoleRequestDto;
import com.example.student.dto.response.RoleResponseDto;

import java.util.List;

public interface RoleService {

        RoleResponseDto createRole(RoleRequestDto requestDto);
        List<RoleResponseDto> getAll();
        void deleteRole(Long role);
        RoleResponseDto updateRole( Long id ,RoleRequestDto requestDto);

}
