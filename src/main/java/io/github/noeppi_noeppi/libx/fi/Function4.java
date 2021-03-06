package io.github.noeppi_noeppi.libx.fi;

import java.util.Objects;
import java.util.function.Function;

/**
 * A function that takes 4 parameters and returns a value.
 */
public interface Function4<A, B, C, D, R> {

    R apply(A a, B b, C c, D d);

    default <V> Function4<A, B, C, D, V> andThen(Function<? super R, ? extends V> after) {
        Objects.requireNonNull(after);
        return (A a, B b, C c, D d) -> after.apply(this.apply(a, b, c, d));
    }
}
