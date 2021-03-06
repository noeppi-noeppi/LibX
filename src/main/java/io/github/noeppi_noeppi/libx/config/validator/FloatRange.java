package io.github.noeppi_noeppi.libx.config.validator;

import java.lang.annotation.*;

/**
 * Config validator that checks whether a float is in a range of allowed values.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface FloatRange {

    /**
     * The lower bound (inclusive)
     */
    float min() default Float.NEGATIVE_INFINITY;
    
    /**
     * The upper bound (inclusive)
     */
    float max() default Float.POSITIVE_INFINITY;
}
