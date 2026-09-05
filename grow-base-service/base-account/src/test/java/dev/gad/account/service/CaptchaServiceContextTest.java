package dev.gad.account.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import dev.gad.account.captcha.CaptchaImageGenerator;
import dev.gad.account.controller.CaptchaController;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

class CaptchaServiceContextTest {

    @Test
    void registersCaptchaBeans() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(
                CaptchaImageGenerator.class, CaptchaService.class, CaptchaController.class)) {
            assertNotNull(context.getBean(CaptchaService.class));
            assertNotNull(context.getBean(CaptchaController.class));
        }
    }
}
