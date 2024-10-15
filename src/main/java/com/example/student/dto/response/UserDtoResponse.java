package com.example.student.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDtoResponse {
    //String id;
    String username;
    String firstName;
    String lastName;
    LocalDate dob;
    Set<RoleResponseDto> roles;
}