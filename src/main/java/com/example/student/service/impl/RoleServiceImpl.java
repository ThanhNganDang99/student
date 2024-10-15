package com.example.student.service.impl;

import com.example.student.dto.request.RoleRequestDto;
import com.example.student.dto.response.RoleResponseDto;
import com.example.student.entity.Permission;
import com.example.student.entity.Role;
import com.example.student.exception.CustomException;
import com.example.student.exception.ErrorCode;
import com.example.student.mapper.RoleMapper;
import com.example.student.repository.PermissionRepository;
import com.example.student.repository.RoleRepository;
import com.example.student.service.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j // logger
public class RoleServiceImpl implements RoleService {

    @Autowired
    RoleRepository roleRepository;
    @Autowired
    RoleMapper roleMapper;
    @Autowired
    PermissionRepository permissionRepository;

    @Override
    public RoleResponseDto createRole(RoleRequestDto requestDto) {
        Role role = roleMapper.toRole(requestDto);
        List<Permission> permissionList = permissionRepository.findAllById(requestDto.getPermission());
        role.setPermission(new HashSet<>(permissionList));
        return roleMapper.toRoleResponseDto(roleRepository.save(role));
    }

    @Override
    public List<RoleResponseDto> getAll() {
        List<Role> roles = roleRepository.findAll();
        return roles.stream().map(roleMapper::toRoleResponseDto).toList();
    }

    @Override
    public void deleteRole(Long role) {
        roleRepository.deleteById(role);
    }

    @Override
    public RoleResponseDto updateRole(Long id, RoleRequestDto requestDto) {
        Role role = roleRepository.findRoleById(id);
        if(ObjectUtils.isEmpty(role)) throw  new CustomException(ErrorCode.ROLE_NOT_EXISTED);
        role= roleMapper.toRole(requestDto);
        role.setId(id);
        List<Permission> permission = permissionRepository.findAllById(requestDto.getPermission());
        if(!CollectionUtils.isEmpty(requestDto.getPermission()) && permission.size()>0){
            if(CollectionUtils.isEmpty(permission)) throw new CustomException(ErrorCode.PERMISSION_NOT_EXISTED);
            role.setPermission(new HashSet<>(permission));
        }
        roleRepository.save(role);
        return roleMapper.toRoleResponseDto(role);
    }
}
