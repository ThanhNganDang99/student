package com.example.student.mapper;

import com.example.student.dto.request.PermissionRequestDto;
import com.example.student.dto.request.RoleRequestDto;
import com.example.student.dto.response.PermissionResponseDto;
import com.example.student.dto.response.RoleResponseDto;
import com.example.student.entity.Permission;
import com.example.student.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    @Mapping(target = "permission", ignore = true)
    Role toRole(RoleRequestDto request);
//    @Mapping(target = "permissions" , ignore = true)
    RoleResponseDto toRoleResponseDto (Role role);

}
