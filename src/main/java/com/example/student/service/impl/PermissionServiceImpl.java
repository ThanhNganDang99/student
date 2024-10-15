package com.example.student.service.impl;

import com.example.student.dto.request.PermissionRequestDto;
import com.example.student.dto.response.PermissionResponseDto;
import com.example.student.entity.Permission;
import com.example.student.mapper.PermissionMapper;
import com.example.student.repository.PermissionRepository;
import com.example.student.service.PermissionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    PermissionMapper permissionMapper;
    @Autowired
    PermissionRepository permissionRepository;

    @Override
    public PermissionResponseDto create(PermissionRequestDto requestDto) {
        Permission permission = permissionMapper.toPermission(requestDto);
        return permissionMapper.toPermissionResponseDto(permissionRepository.save(permission));
    }

    @Override
    public List<PermissionResponseDto> getAll() {
        return permissionRepository.findAll().stream().map(permissionMapper::toPermissionResponseDto).toList();
    }

    @Override
    public void deletePermission(Long permission) {
        permissionRepository.deleteById(permission);

    }
}
