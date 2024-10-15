package com.example.student.exception;

import com.example.student.dto.response.ApiResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

import org.hibernate.engine.internal.Collections;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.lang.reflect.Field;
import java.util.*;

import static org.hibernate.query.sqm.tree.SqmNode.log;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandle {

    private static final String MIN_ATTRIBUTE = "min";

    ///  trường hợp exception còn lại
    /*@ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse> handlingRuntimeException(RuntimeException e) {
        ApiResponse apiResponse = new ApiResponse<>();
        apiResponse.setCode(ErrorCode.UNCATEGORIZED_EXCEPTION.getCode());
        apiResponse.setMessage(ErrorCode.UNCATEGORIZED_EXCEPTION.getMessage());
        return ResponseEntity.badRequest().body(apiResponse);
    }*/

    @ExceptionHandler(value = ConstraintViolationException.class)
    ResponseEntity<ApiResponse> handlingConstraintViolationException(ConstraintViolationException e) {
        ApiResponse apiResponse = new ApiResponse<>();
        List<String> errorMessages = e.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .toList();
        ErrorCode errorCode = ErrorCode.INVALID_DOB;
        Map<String, Object> attributes = e.getConstraintViolations()
                .stream().toList().get(0).getConstraintDescriptor().getAttributes();
        try {
            errorCode = ErrorCode.valueOf(errorMessages.get(0));
        } catch (IllegalArgumentException ignored) {
        }
        String message = mapAttribute(errorCode.getMessage(), attributes);
        apiResponse.setMessage(message);
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setStatusCode(errorCode.getStatusCode());
        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = CustomException.class)
    ResponseEntity<ApiResponse> handlingCustomException(CustomException e) {
        ApiResponse apiResponse = new ApiResponse<>();
        ErrorCode errorCode = ErrorCode.INVALID_DOB;
        try {
            errorCode = ErrorCode.valueOf(e.getErrorCode().toString());
        } catch (Exception e1) {
        }
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());
        //    apiResponse.setStatusCode(errorCode.getStatusCode());
        return ResponseEntity.status(errorCode.getStatusCode()).body(apiResponse);
    }

    private String mapAttribute(String message, Map<String, Object> attributes) {
        String minValue = String.valueOf(attributes.get(MIN_ATTRIBUTE));
        return message.replace("{" + MIN_ATTRIBUTE + "}", minValue);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse> handlingValidation(MethodArgumentNotValidException exception) {
        String enumKey = exception.getFieldError().getDefaultMessage();

        ErrorCode errorCode = ErrorCode.INVALID_KEY;
        Map<String, Object> attributes = null;
        try {
            errorCode = ErrorCode.valueOf(enumKey);

            var constraintViolation =
                    exception.getBindingResult().getAllErrors().stream().toList().get(0).unwrap(ConstraintViolation.class);

            attributes = constraintViolation.getConstraintDescriptor().getAttributes();

            log.info(attributes.toString());

        } catch (IllegalArgumentException e) {

        }

        ApiResponse apiResponse = new ApiResponse();

        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(
                Objects.nonNull(attributes)
                        ? mapAttribute(errorCode.getMessage(), attributes)
                        : errorCode.getMessage());

        return ResponseEntity.badRequest().body(apiResponse);
    }


}
