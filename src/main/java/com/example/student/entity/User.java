package com.example.student.entity;

import com.example.student.validator.DobConstraint;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.mapstruct.Mapper;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Mapper
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @Size(min = 5, message = "USERNAME_INVALID")
    @NotBlank(message = "USERNAME_NOT_BLANK")
    String username;

    @Size(min = 5, message = "INVALID_PASSWORD")
    @NotBlank(message = "PASSWORD_NOT_BLANK")
    String password;

    String firstName;
    String lastName;

    @DobConstraint(min = 18, message = "INVALID_DOB")
    LocalDate dob;
    @ManyToMany
    Set<Role> roles;

}
