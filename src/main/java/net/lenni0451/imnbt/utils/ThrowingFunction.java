package net.lenni0451.imnbt.utils;

/**
 * A function that can throw an exception.
 *
 * @param <A> The argument type
 * @param <R> The return type
 */
@FunctionalInterface
public interface ThrowingFunction<A, R> {

    R apply(final A a) throws Throwable;

}
