package dev.gad.account.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "CaptchaResponse", description = "图片验证码及其本次校验标识")
public record CaptchaResponse(
        @Schema(description = "验证码唯一标识", example = "550e8400-e29b-41d4-a716-446655440000")
        String captchaId,
        @Schema(description = "PNG格式的Base64图片Data URI")
        String imageBase64) {
}
