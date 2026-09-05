package dev.gad.infra.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

class JacksonConfigTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        Jackson2ObjectMapperBuilder builder = Jackson2ObjectMapperBuilder.json();
        new JacksonConfig().jacksonTimeCustomizer().customize(builder);
        objectMapper = builder.build();
    }

    @Test
    void serializesAndDeserializesJavaTimeTypes() throws Exception {
        LocalDate date = LocalDate.of(2026, 9, 5);
        LocalTime time = LocalTime.of(14, 30, 45);
        LocalDateTime dateTime = LocalDateTime.of(date, time);

        assertEquals("\"2026-09-05\"", objectMapper.writeValueAsString(date));
        assertEquals("\"14:30:45\"", objectMapper.writeValueAsString(time));
        assertEquals("\"2026-09-05 14:30:45\"", objectMapper.writeValueAsString(dateTime));
        assertEquals(date, objectMapper.readValue("\"2026-09-05\"", LocalDate.class));
        assertEquals(time, objectMapper.readValue("\"14:30:45\"", LocalTime.class));
        assertEquals(dateTime,
                objectMapper.readValue("\"2026-09-05 14:30:45\"", LocalDateTime.class));
    }

    @Test
    void appliesShanghaiTimeZoneToLegacyDate() throws Exception {
        Date date = Date.from(LocalDateTime.of(2026, 9, 5, 14, 30, 45)
                .atZone(ZoneId.of(JacksonConfig.TIME_ZONE))
                .toInstant());

        assertEquals("\"2026-09-05 14:30:45\"", objectMapper.writeValueAsString(date));
        assertEquals(date,
                objectMapper.readValue("\"2026-09-05 14:30:45\"", Date.class));
    }
}
