package com.example.student.service;

import com.example.student.dto.request.UserDtoRequest;
import com.example.student.dto.response.ApiResponse;
import com.example.student.dto.response.UserDtoResponse;
import com.example.student.entity.User;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.mapstruct.control.MappingControl;
import org.springframework.stereotype.Service;

import java.util.List;


public interface UserService {
    public UserDtoResponse createUser(UserDtoRequest request);
    public UserDtoResponse update(UserDtoRequest request, Long id);
    public List<UserDtoResponse> getAllUser();
    public UserDtoResponse getUser(Long id);
    public UserDtoResponse myInfo();

}
