package com.example.student.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

//@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDtoRequest {
    String username;
    @Size(min = 5, message = "INVALID_PASSWORD")
    @NotBlank(message = "PASSWORD_NOT_BLANK")
    String password;
    String firstName;
    String lastName;
    LocalDate dob;
    Set<Long> role;


}
