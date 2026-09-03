package dev.gad.utils.support.io;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import dev.gad.utils.cache.LocalLRUCache;

import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * @author LIANGQI
 * @version 1.0
 */
public class BigDecimalJsonSerializer extends JsonSerializer<BigDecimal> {

    private static final LocalLRUCache<String, DecimalFormat> FORMAT_VALUE_CACHE =  new LocalLRUCache<>(100);
    private static final DecimalFormat DEFAULT_FORMATTER = new DecimalFormat("#0.00");

    static {
        DEFAULT_FORMATTER.setRoundingMode(RoundingMode.HALF_UP);
    }

    @Override
    public void serialize(BigDecimal value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(foundDecimalFormat(gen).format(value));
    }

    private DecimalFormat foundDecimalFormat(JsonGenerator gen) {
        String filedName = gen.getOutputContext().getCurrentName();
        Object currentValue = gen.getOutputContext().getCurrentValue();
        Class<?> currentClass  = currentValue.getClass();
        String cacheKey = String.join("_", currentClass.getName(), filedName);
        if(!FORMAT_VALUE_CACHE.containsKey(cacheKey)) {
            Field[] declaredFields = currentClass.getDeclaredFields();
            DecimalField decimalFiled = null;
            for (Field declaredField : declaredFields) {
                if (declaredField.getName().equals(filedName)) {
                    decimalFiled = declaredField.getAnnotation(DecimalField.class);
                }
            }
            if(decimalFiled == null) {
                FORMAT_VALUE_CACHE.put(cacheKey, DEFAULT_FORMATTER);
            } else {
                DecimalFormat decimalFormat = new DecimalFormat(decimalFiled.value());
                decimalFormat.setRoundingMode(decimalFiled.roundingMode());
                FORMAT_VALUE_CACHE.put(cacheKey, decimalFormat);
            }
        }
        return FORMAT_VALUE_CACHE.get(cacheKey);
    }
}

