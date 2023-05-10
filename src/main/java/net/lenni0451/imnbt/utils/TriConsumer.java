package net.lenni0451.imnbt.utils;

/**
 * A consumer consuming three arguments.
 *
 * @param <A> The first argument type
 * @param <B> The second argument type
 * @param <C> The third argument type
 */
@FunctionalInterface
public interface TriConsumer<A, B, C> {

    void accept(final A a, final B b, final C c);

}
