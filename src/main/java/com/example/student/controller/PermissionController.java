package com.example.student.controller;

import com.example.student.dto.request.PermissionRequestDto;
import com.example.student.dto.response.ApiResponse;
import com.example.student.dto.response.PermissionResponseDto;
import com.example.student.service.PermissionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/permission")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@RestController
public class PermissionController {

    PermissionService permissionService;

    @PostMapping("/create")
    ApiResponse<PermissionResponseDto> create(@RequestBody PermissionRequestDto request) {
        return ApiResponse.<PermissionResponseDto>builder()
                .result(permissionService.create(request))
                .build();
    }

    @GetMapping
    ApiResponse<List<PermissionResponseDto>> getAll() {
        return ApiResponse.<List<PermissionResponseDto>>builder()
                .result(permissionService.getAll())
                .build();
    }

    @DeleteMapping("/{permission}")
    ApiResponse<Void> delete(@PathVariable Long permission) {
        permissionService.deletePermission(permission);
        return ApiResponse.<Void>builder().build();
    }


}
