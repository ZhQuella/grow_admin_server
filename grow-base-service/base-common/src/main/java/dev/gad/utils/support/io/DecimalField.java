package dev.gad.utils.support.io;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.math.RoundingMode;

@Target({ElementType.ANNOTATION_TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER,
        ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DecimalField {

    public String value() default "#0.00";

    public RoundingMode roundingMode() default RoundingMode.HALF_UP;
}
