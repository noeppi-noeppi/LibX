package io.github.noeppi_noeppi.libx.annotation;

import java.lang.annotation.*;

/**
 * Marks a parameter to be supplied by a registry lookup codec. Can only
 * be used on parameters of type {@code Registry}.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.PARAMETER)
@Documented
public @interface Lookup {

    /**
     * The path of the registry id. If left empty, it'll be auto-detected.
     */
    String value() default "";
    
    /**
     * The namespace of the registry id. Defaults to {@code minecraft}.
     */
    String namespace() default "minecraft";
}
