package dev.gad.infra.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import dev.gad.common.result.Result;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.MethodParameter;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.method.MethodValidationResult;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.server.ServerWebInputException;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handlesDtoValidationError() throws NoSuchMethodException {
        MethodParameter parameter = methodParameter();
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "request");
        bindingResult.addError(new FieldError(
                "request", "email", "invalid", false, null, null, "邮箱格式错误"));

        Result<Void> result = handler.handleWebExchangeBindException(
                new WebExchangeBindException(parameter, bindingResult));

        assertEquals(400, result.getCode());
        assertEquals("email: 邮箱格式错误", result.getMessage());
    }

    @Test
    void handlesMethodParameterValidationError() throws NoSuchMethodException {
        MethodParameter parameter = methodParameter();
        ParameterValidationResult parameterResult = new ParameterValidationResult(
                parameter,
                "",
                List.of(new DefaultMessageSourceResolvable(new String[0], "不能为空")));
        MethodValidationResult validationResult = MethodValidationResult.create(
                this,
                parameter.getMethod(),
                List.of(parameterResult));

        Result<Void> result = handler.handleHandlerMethodValidationException(
                new HandlerMethodValidationException(validationResult));

        assertEquals(400, result.getCode());
        assertEquals("不能为空", result.getMessage());
    }

    @Test
    void handlesConstraintViolation() {
        ConstraintViolation<?> violation = constraintViolation("create.email", "邮箱格式错误");

        Result<Void> result = handler.handleConstraintViolationException(
                new ConstraintViolationException(Set.of(violation)));

        assertEquals(400, result.getCode());
        assertEquals("create.email: 邮箱格式错误", result.getMessage());
    }

    @Test
    void handlesRequestParsingError() {
        Result<Void> result = handler.handleServerWebInputException(
                new ServerWebInputException("Failed to read request"));

        assertEquals(400, result.getCode());
        assertEquals("请求参数错误", result.getMessage());
    }

    private MethodParameter methodParameter() throws NoSuchMethodException {
        Method method = getClass().getDeclaredMethod("validatedEndpoint", String.class);
        return new MethodParameter(method, 0);
    }

    private ConstraintViolation<?> constraintViolation(String path, String message) {
        Path propertyPath = (Path) Proxy.newProxyInstance(
                Path.class.getClassLoader(),
                new Class<?>[] {Path.class},
                (proxy, method, args) -> method.getName().equals("toString") ? path : null);
        return (ConstraintViolation<?>) Proxy.newProxyInstance(
                ConstraintViolation.class.getClassLoader(),
                new Class<?>[] {ConstraintViolation.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "getPropertyPath" -> propertyPath;
                    case "getMessage" -> message;
                    case "hashCode" -> System.identityHashCode(proxy);
                    case "equals" -> proxy == args[0];
                    case "toString" -> path + ": " + message;
                    default -> null;
                });
    }

    private void validatedEndpoint(String email) {
    }
}
