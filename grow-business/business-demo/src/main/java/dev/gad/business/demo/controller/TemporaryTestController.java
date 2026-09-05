package dev.gad.business.demo.controller;

import dev.gad.business.common.enums.BusinessErrorCode;
import dev.gad.common.exception.BusinessException;
import dev.gad.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@Tag(name = "演示与测试", description = "用于验证基础设施能力的临时测试接口")
public class TemporaryTestController {

    @GetMapping("/business-exception")
    @Operation(
            summary = "测试统一业务异常",
            description = "主动抛出BusinessException，用于检查统一JSON异常响应。")
    public Result<Void> throwBusinessException() {
        throw new BusinessException(BusinessErrorCode.CUSTOMER_NOT_FOUND);
    }
}
