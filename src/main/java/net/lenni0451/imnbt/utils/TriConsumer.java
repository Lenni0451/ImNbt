package net.lenni0451.imnbt.utils;

@FunctionalInterface
public interface TriConsumer<A, B, C> {

    void accept(final A a, final B b, final C c);

}
