package com.example.student.service;

import com.example.student.dto.request.AuthenticationDtoRequest;
import com.example.student.dto.request.IntrospectRequestDto;
import com.example.student.dto.response.AuthenticationDtoResponse;
import com.example.student.dto.response.IntrospectResponseDto;
import com.nimbusds.jose.JOSEException;

import java.text.ParseException;

public interface AuthenticationService {
    AuthenticationDtoResponse authenticate(AuthenticationDtoRequest authenticationDtoRequest);
    IntrospectResponseDto introspect(IntrospectRequestDto requestDto) throws JOSEException, ParseException;
    void logout( IntrospectRequestDto requestDto) throws ParseException, JOSEException;
    AuthenticationDtoResponse refreshToken(IntrospectRequestDto requestDto) throws JOSEException, ParseException;
}
