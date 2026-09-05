package dev.gad.business.common.enums;

import dev.gad.common.result.ErrorCode;

public enum BusinessErrorCode implements ErrorCode {

    CUSTOMER_NOT_FOUND(20001, "客户不存在");

    private final Integer code;

    private final String message;

    BusinessErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}