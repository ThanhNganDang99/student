package com.example.student.validator;

import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Size;

import java.lang.annotation.*;
//mục tiêu valida đến những chỗ nào => tham khảo anotation validate của spring boot
@Target({ ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME) // => valida này sẽ xử lý lúc nào => đang chọn sẽ xử lý lúc runtime
@Constraint(validatedBy = {DobValidator.class}) // => xử lý valida

public @interface DobConstraint { // hàm này chỉ có nhiệm vụ khai báo thôi

    String message() default "Invalid date of birth";

    int min();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
