package com.example.student.mapper;

import com.example.student.dto.request.UserDtoRequest;
import com.example.student.dto.response.UserDtoResponse;
import com.example.student.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "roles", ignore = true)
    User toUser(UserDtoRequest request);

    @Mapping(target = "roles", ignore = true)
    void updateUser(@MappingTarget User user, UserDtoRequest request);

//    @Mapping(target = "roles", ignore = true)
    UserDtoResponse toUserDtoResponse(User user);

    List<UserDtoResponse> toUserDtoResponseList(List<User> users);

}
