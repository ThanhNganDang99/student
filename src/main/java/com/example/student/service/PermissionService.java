package com.example.student.service;

import com.example.student.dto.request.PermissionRequestDto;
import com.example.student.dto.response.PermissionResponseDto;

import java.util.List;

public interface PermissionService {

    PermissionResponseDto create(PermissionRequestDto requestDto);
    List<PermissionResponseDto> getAll();
    void deletePermission (Long permission);

}
