package com.example.student.controller;

import com.example.student.dto.request.AuthenticationDtoRequest;
import com.example.student.dto.request.IntrospectRequestDto;
import com.example.student.dto.response.ApiResponse;
import com.example.student.dto.response.AuthenticationDtoResponse;
import com.example.student.dto.response.IntrospectResponseDto;
import com.example.student.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.method.P;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor // Autowrite cac bean
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {

    AuthenticationService authenticationService;


    @PostMapping("/log-in")
    ApiResponse<AuthenticationDtoResponse> authenticate(@RequestBody AuthenticationDtoRequest request) {
        AuthenticationDtoResponse authenticationDtoResponse = authenticationService.authenticate(request);
        return ApiResponse.<AuthenticationDtoResponse>builder()
                .result
                        (
                                authenticationDtoResponse
                        )
                .build();
    }

    @PostMapping("/refresh")
    ApiResponse<AuthenticationDtoResponse> refreshToken (@RequestBody IntrospectRequestDto request) throws ParseException, JOSEException {
        AuthenticationDtoResponse authenticationDtoResponse = authenticationService.refreshToken(request);
        return ApiResponse.<AuthenticationDtoResponse>builder()
                .result(authenticationDtoResponse)
                .build();
    }

    @PostMapping("/introspect")
    ApiResponse<IntrospectResponseDto> authenticate(@RequestBody IntrospectRequestDto request) throws ParseException, JOSEException {
       var result = authenticationService.introspect(request);
        return ApiResponse.<IntrospectResponseDto>builder()
                .result(result)
                .build();
    }


    @PostMapping("/logout")
     ApiResponse<Void> logout(@RequestBody IntrospectRequestDto requestDto) throws ParseException, JOSEException {
        authenticationService.logout(requestDto);
        return ApiResponse.<Void>builder()
                .build();

    }

}
