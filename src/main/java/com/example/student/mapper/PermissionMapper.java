package com.example.student.mapper;

import com.example.student.dto.request.PermissionRequestDto;
import com.example.student.dto.response.PermissionResponseDto;
import com.example.student.entity.Permission;
import jakarta.persistence.ManyToMany;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequestDto request);
    PermissionResponseDto toPermissionResponseDto (Permission permission);

}
