package dev.gad.account.controller;

import dev.gad.account.dto.CaptchaResponse;
import dev.gad.account.service.CaptchaService;
import dev.gad.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.CacheControl;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.WebSession;

@RestController
@RequestMapping("/account")
@Tag(name = "账号与登录", description = "账号登录、身份认证和验证码相关接口")
public class CaptchaController {

    private final CaptchaService captchaService;

    public CaptchaController(CaptchaService captchaService) {
        this.captchaService = captchaService;
    }

    @GetMapping("/captcha")
    @Operation(
            summary = "获取图片验证码",
            description = "生成4位大小写字母或数字验证码，保存到当前Session，有效期2分钟。")
    public Result<CaptchaResponse> getCaptcha(
            WebSession session, ServerHttpResponse response) {
        response.getHeaders().setCacheControl(CacheControl.noStore().getHeaderValue());
        return Result.success(captchaService.createCaptcha(session));
    }
}
