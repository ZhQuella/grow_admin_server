package dev.gad.account.service;

import dev.gad.account.captcha.CaptchaImageGenerator;
import dev.gad.account.dto.CaptchaResponse;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.server.WebSession;

@Service
public class CaptchaService {

    public static final Duration CAPTCHA_TTL = Duration.ofMinutes(2);

    private static final String SESSION_ATTRIBUTE = CaptchaService.class.getName() + ".captcha";

    private final CaptchaImageGenerator imageGenerator;
    private final Clock clock;

    @Autowired
    public CaptchaService(CaptchaImageGenerator imageGenerator) {
        this(imageGenerator, Clock.systemUTC());
    }

    CaptchaService(CaptchaImageGenerator imageGenerator, Clock clock) {
        this.imageGenerator = imageGenerator;
        this.clock = clock;
    }

    public CaptchaResponse createCaptcha(WebSession session) {
        CaptchaImageGenerator.GeneratedCaptcha generatedCaptcha = imageGenerator.generate();
        String captchaId = UUID.randomUUID().toString();
        SessionCaptcha sessionCaptcha = new SessionCaptcha(
                captchaId,
                generatedCaptcha.answer(),
                clock.instant().plus(CAPTCHA_TTL));
        session.start();
        session.getAttributes().put(SESSION_ATTRIBUTE, sessionCaptcha);
        return new CaptchaResponse(captchaId, generatedCaptcha.imageBase64());
    }

    public boolean verify(WebSession session, String captchaId, String answer) {
        Object storedValue = session.getAttributes().remove(SESSION_ATTRIBUTE);
        if (!(storedValue instanceof SessionCaptcha storedCaptcha)) {
            return false;
        }
        if (!clock.instant().isBefore(storedCaptcha.expiresAt())) {
            return false;
        }
        return storedCaptcha.captchaId().equals(captchaId)
                && answer != null
                && storedCaptcha.answer().equals(answer.trim());
    }

    private record SessionCaptcha(String captchaId, String answer, Instant expiresAt) {
    }
}
