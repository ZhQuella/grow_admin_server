package dev.gad.security.jwt;

import dev.gad.common.exception.BusinessException;
import dev.gad.common.result.ResultCode;

public class JwtAuthenticationException extends BusinessException {

    public JwtAuthenticationException(String message) {
        super(ResultCode.UNAUTHORIZED.getCode(), message);
    }
}
