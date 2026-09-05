package dev.gad.account.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import dev.gad.account.captcha.CaptchaImageGenerator;
import dev.gad.account.dto.CaptchaResponse;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.server.MockWebSession;

class CaptchaServiceTest {

    private static final Instant NOW = Instant.parse("2026-09-05T06:30:45Z");

    private CaptchaImageGenerator imageGenerator;
    private MockWebSession session;

    @BeforeEach
    void setUp() {
        imageGenerator = mock(CaptchaImageGenerator.class);
        when(imageGenerator.generate()).thenReturn(
                new CaptchaImageGenerator.GeneratedCaptcha("aB7Z", "data:image/png;base64,image"));
        session = new MockWebSession();
    }

    @Test
    void verifiesCaptchaOnlyOnce() {
        CaptchaService service = serviceAt(NOW);
        CaptchaResponse response = service.createCaptcha(session);

        assertTrue(service.verify(session, response.captchaId(), "aB7Z"));
        assertFalse(service.verify(session, response.captchaId(), "aB7Z"));
    }

    @Test
    void rejectsExpiredCaptcha() {
        CaptchaResponse response = serviceAt(NOW).createCaptcha(session);

        assertFalse(serviceAt(NOW.plus(CaptchaService.CAPTCHA_TTL))
                .verify(session, response.captchaId(), "aB7Z"));
    }

    @Test
    void consumesCaptchaAfterWrongAnswer() {
        CaptchaService service = serviceAt(NOW);
        CaptchaResponse response = service.createCaptcha(session);

        assertFalse(service.verify(session, response.captchaId(), "ab7z"));
        assertFalse(service.verify(session, response.captchaId(), "aB7Z"));
    }

    private CaptchaService serviceAt(Instant instant) {
        return new CaptchaService(imageGenerator, Clock.fixed(instant, ZoneOffset.UTC));
    }
}
