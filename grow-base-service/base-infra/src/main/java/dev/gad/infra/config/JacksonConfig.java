package dev.gad.infra.config;

import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class JacksonConfig {

    public static final String DATE_PATTERN = "yyyy-MM-dd";
    public static final String TIME_PATTERN = "HH:mm:ss";
    public static final String DATE_TIME_PATTERN = DATE_PATTERN + " " + TIME_PATTERN;
    public static final String TIME_ZONE = "Asia/Shanghai";

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonTimeCustomizer() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(DATE_PATTERN);
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern(TIME_PATTERN);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
        SimpleDateFormat legacyDateFormat = new SimpleDateFormat(DATE_TIME_PATTERN);
        legacyDateFormat.setTimeZone(TimeZone.getTimeZone(TIME_ZONE));

        return builder -> builder
                .dateFormat(legacyDateFormat)
                .timeZone(TimeZone.getTimeZone(TIME_ZONE))
                .serializers(
                        new LocalDateSerializer(dateFormatter),
                        new LocalTimeSerializer(timeFormatter),
                        new LocalDateTimeSerializer(dateTimeFormatter))
                .deserializers(
                        new LocalDateDeserializer(dateFormatter),
                        new LocalTimeDeserializer(timeFormatter),
                        new LocalDateTimeDeserializer(dateTimeFormatter));
    }
}
