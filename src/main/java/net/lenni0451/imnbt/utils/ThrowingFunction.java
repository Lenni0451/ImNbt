package net.lenni0451.imnbt.utils;

@FunctionalInterface
public interface ThrowingFunction<A, R> {

    R apply(final A a) throws Throwable;

}
