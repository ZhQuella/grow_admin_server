package dev.gad.infra.exception;

import dev.gad.common.exception.BusinessException;
import dev.gad.common.result.Result;
import dev.gad.common.result.ResultCode;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.Objects;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.server.ServerWebInputException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException exception) {
        LOGGER.warn("Business exception: code={}, message={}",
                exception.getCode(), exception.getMessage());
        return Result.fail(exception.getCode(), exception.getMessage());
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public Result<Void> handleWebExchangeBindException(WebExchangeBindException exception) {
        String message = exception.getAllErrors().stream()
                .map(this::formatBindingError)
                .collect(Collectors.joining("; "));
        LOGGER.warn("Request body validation failed: {}", message);
        return badRequest(message);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public Result<Void> handleHandlerMethodValidationException(
            HandlerMethodValidationException exception) {
        String message = exception.getAllValidationResults().stream()
                .map(this::formatParameterValidationResult)
                .filter(result -> !result.isBlank())
                .collect(Collectors.joining("; "));
        LOGGER.warn("Method parameter validation failed: {}", message);
        return badRequest(message);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public Result<Void> handleConstraintViolationException(
            ConstraintViolationException exception) {
        String message = exception.getConstraintViolations().stream()
                .map(this::formatConstraintViolation)
                .collect(Collectors.joining("; "));
        LOGGER.warn("Constraint validation failed: {}", message);
        return badRequest(message);
    }

    @ExceptionHandler(ServerWebInputException.class)
    public Result<Void> handleServerWebInputException(ServerWebInputException exception) {
        LOGGER.warn("Request input error: {}", exception.getReason());
        return Result.fail(ResultCode.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception exception) {
        LOGGER.error("Unhandled exception", exception);
        return Result.fail(ResultCode.ERROR);
    }

    private String formatBindingError(ObjectError error) {
        String field = error instanceof FieldError fieldError ? fieldError.getField() : null;
        return formatValidationMessage(field, error.getDefaultMessage());
    }

    private String formatParameterValidationResult(ParameterValidationResult result) {
        String parameter = result.getMethodParameter().getParameterName();
        return result.getResolvableErrors().stream()
                .map(MessageSourceResolvable::getDefaultMessage)
                .filter(Objects::nonNull)
                .map(message -> formatValidationMessage(parameter, message))
                .collect(Collectors.joining("; "));
    }

    private String formatConstraintViolation(ConstraintViolation<?> violation) {
        return formatValidationMessage(violation.getPropertyPath().toString(), violation.getMessage());
    }

    private String formatValidationMessage(String field, String message) {
        if (message == null || message.isBlank()) {
            return ResultCode.BAD_REQUEST.getMessage();
        }
        return field == null || field.isBlank() ? message : field + ": " + message;
    }

    private Result<Void> badRequest(String message) {
        if (message == null || message.isBlank()) {
            return Result.fail(ResultCode.BAD_REQUEST);
        }
        return Result.fail(ResultCode.BAD_REQUEST.getCode(), message);
    }
}
