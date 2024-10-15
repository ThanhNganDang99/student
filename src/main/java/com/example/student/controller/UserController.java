package com.example.student.controller;

import com.example.student.dto.request.UserDtoRequest;
import com.example.student.dto.response.ApiResponse;
import com.example.student.dto.response.UserDtoResponse;
import com.example.student.mapper.UserMapper;
import com.example.student.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserController {
    UserService userService;
    UserMapper userMapper;

    @PostMapping("/create")
    ApiResponse<UserDtoResponse> createUser(@RequestBody @Valid UserDtoRequest request) {
        return ApiResponse.<UserDtoResponse>builder()
                .result(userService.createUser(request))
                .build();
    }

    @PostMapping("/update/{id}")
    ApiResponse<UserDtoResponse> update(@PathVariable Long id, @RequestBody UserDtoRequest request) {
        return ApiResponse.<UserDtoResponse>builder().result(userService.update(request, id)).build();
    }

    @GetMapping("/get")
    ApiResponse<List<UserDtoResponse>> getAllUser(){
        var authentication = SecurityContextHolder.getContext().getAuthentication();
      authentication.getAuthorities().forEach(x->
              log.info(x.getAuthority()));
        return ApiResponse.<List<UserDtoResponse>>builder().result(userService.getAllUser()).build();
    }

    @GetMapping("/get_user/{id}")
    ApiResponse<UserDtoResponse> getUser(@PathVariable Long id){
        return ApiResponse.<UserDtoResponse>builder().result(userService.getUser(id)).build();
    }
    @GetMapping("/myInfo")
    ApiResponse<UserDtoResponse> myInfo(){
        return ApiResponse.<UserDtoResponse>builder().result(userService.myInfo()).build();
    }
}
