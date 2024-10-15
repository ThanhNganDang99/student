package com.example.student.dto.request;

import com.example.student.entity.Permission;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Set;
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleRequestDto {
    String name;
    String description;
    Set<Long> permission;
}
