package com.example.student.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleResponseDto {
    String name;
    String description;
    Set<PermissionResponseDto> permission;
}
