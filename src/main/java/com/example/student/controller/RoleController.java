package com.example.student.controller;

import com.example.student.dto.request.RoleRequestDto;
import com.example.student.dto.response.ApiResponse;
import com.example.student.dto.response.RoleResponseDto;
import com.example.student.service.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/role")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@RestController
public class RoleController {
    RoleService roleService;

    @PostMapping("/create")
    public ApiResponse<RoleResponseDto> create(@RequestBody RoleRequestDto requestDto){
        return  ApiResponse.<RoleResponseDto>builder()
                .result(roleService.createRole(requestDto))
                .build();
    }
    @GetMapping("/getAll")
    public ApiResponse<List<RoleResponseDto>> getAll() {
        return ApiResponse.<List<RoleResponseDto>>builder()
                .result(roleService.getAll())
                .build();
    }

    @DeleteMapping("/{role}")
    ApiResponse<Void> delete(@PathVariable Long role) {
        roleService.deleteRole(role);
        return ApiResponse.<Void>builder().build();
    }


    @PostMapping("/update")
    public ApiResponse<RoleResponseDto> update(@RequestParam Long id, @RequestBody RoleRequestDto roleRequestDto){
        return  ApiResponse.<RoleResponseDto>builder()
                .result(roleService.updateRole(id,roleRequestDto))
                .build();
    }
}
