package dev.gad.account.captcha;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.Base64;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.Test;

class CaptchaImageGeneratorTest {

    @Test
    void generatesReadablePngDataUri() throws Exception {
        CaptchaImageGenerator.GeneratedCaptcha captcha =
                new CaptchaImageGenerator().generate();

        assertTrue(captcha.answer().matches("[A-Za-z0-9]{4}"));
        assertTrue(CaptchaImageGenerator.CAPTCHA_CHARACTERS.chars()
                .anyMatch(Character::isUpperCase));
        assertTrue(CaptchaImageGenerator.CAPTCHA_CHARACTERS.chars()
                .anyMatch(Character::isLowerCase));
        assertTrue(CaptchaImageGenerator.CAPTCHA_CHARACTERS.chars()
                .anyMatch(Character::isDigit));
        assertTrue(captcha.imageBase64().startsWith("data:image/png;base64,"));
        byte[] imageBytes = Base64.getDecoder().decode(
                captcha.imageBase64().substring("data:image/png;base64,".length()));
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
        assertNotNull(image);
        assertEquals(CaptchaImageGenerator.WIDTH, image.getWidth());
        assertEquals(CaptchaImageGenerator.HEIGHT, image.getHeight());
    }
}
