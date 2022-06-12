package com.travel.demo.constans.exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class BusinessException extends RuntimeException {
    private final String code;
    private final String msg;

    public BusinessException(ExceptionCode exception) {
        this.code = exception.name();
        this.msg = exception.getMessage();
    }
}
