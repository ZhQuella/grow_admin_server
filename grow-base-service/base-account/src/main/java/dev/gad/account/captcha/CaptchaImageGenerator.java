package dev.gad.account.captcha;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.CubicCurve2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;
import javax.imageio.ImageIO;
import org.springframework.stereotype.Component;

@Component
public class CaptchaImageGenerator {

    public static final int WIDTH = 140;
    public static final int HEIGHT = 48;

    private static final int CODE_LENGTH = 4;
    static final String CAPTCHA_CHARACTERS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final String DATA_URI_PREFIX = "data:image/png;base64,";
    private static final Color[] TEXT_COLORS = {
        new Color(30, 64, 175),
        new Color(15, 118, 110),
        new Color(190, 24, 93),
        new Color(109, 40, 217)
    };

    private final SecureRandom random = new SecureRandom();

    public GeneratedCaptcha generate() {
        String answer = generateAnswer();
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        try {
            configureRendering(graphics);
            drawBackground(graphics);
            drawNoise(graphics);
            drawAnswer(graphics, answer);
        } finally {
            graphics.dispose();
        }
        return new GeneratedCaptcha(answer, encode(image));
    }

    private String generateAnswer() {
        StringBuilder answer = new StringBuilder(CODE_LENGTH);
        for (int index = 0; index < CODE_LENGTH; index++) {
            answer.append(CAPTCHA_CHARACTERS.charAt(random.nextInt(CAPTCHA_CHARACTERS.length())));
        }
        return answer.toString();
    }

    private void configureRendering(Graphics2D graphics) {
        graphics.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics.setRenderingHint(
                RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    }

    private void drawBackground(Graphics2D graphics) {
        GradientPaint background = new GradientPaint(
                0, 0, new Color(239, 246, 255),
                WIDTH, HEIGHT, new Color(250, 245, 255));
        graphics.setPaint(background);
        graphics.fillRect(0, 0, WIDTH, HEIGHT);

        graphics.setColor(new Color(255, 255, 255, 150));
        graphics.fillOval(-18, -24, 82, 60);
        graphics.fillOval(94, 18, 70, 50);
    }

    private void drawNoise(Graphics2D graphics) {
        for (int index = 0; index < 34; index++) {
            int alpha = 35 + random.nextInt(45);
            graphics.setColor(new Color(
                    random.nextInt(130), random.nextInt(130), random.nextInt(180), alpha));
            int diameter = 1 + random.nextInt(3);
            graphics.fillOval(random.nextInt(WIDTH), random.nextInt(HEIGHT), diameter, diameter);
        }

        graphics.setStroke(new BasicStroke(1.2F));
        for (int index = 0; index < 3; index++) {
            graphics.setColor(new Color(
                    40 + random.nextInt(100),
                    60 + random.nextInt(100),
                    130 + random.nextInt(100),
                    75));
            graphics.draw(new CubicCurve2D.Float(
                    -10, random.nextInt(HEIGHT),
                    35, random.nextInt(HEIGHT),
                    95, random.nextInt(HEIGHT),
                    WIDTH + 10, random.nextInt(HEIGHT)));
        }
    }

    private void drawAnswer(Graphics2D graphics, String answer) {
        Font font = new Font(Font.SANS_SERIF, Font.BOLD, 30);
        graphics.setFont(font);
        FontMetrics metrics = graphics.getFontMetrics();
        int baseline = (HEIGHT - metrics.getHeight()) / 2 + metrics.getAscent();

        for (int index = 0; index < answer.length(); index++) {
            String character = String.valueOf(answer.charAt(index));
            int x = 17 + index * 28;
            double angle = Math.toRadians(random.nextDouble() * 20 - 10);
            Graphics2D characterGraphics = (Graphics2D) graphics.create();
            try {
                characterGraphics.rotate(angle, x + 9, HEIGHT / 2.0);
                characterGraphics.setColor(new Color(255, 255, 255, 180));
                characterGraphics.drawString(character, x + 1, baseline + 1);
                characterGraphics.setColor(TEXT_COLORS[random.nextInt(TEXT_COLORS.length)]);
                characterGraphics.drawString(character, x, baseline);
            } finally {
                characterGraphics.dispose();
            }
        }
    }

    private String encode(BufferedImage image) {
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", output);
            return DATA_URI_PREFIX + Base64.getEncoder().encodeToString(output.toByteArray());
        } catch (IOException exception) {
            throw new IllegalStateException("验证码图片生成失败", exception);
        }
    }

    public record GeneratedCaptcha(String answer, String imageBase64) {
    }
}
