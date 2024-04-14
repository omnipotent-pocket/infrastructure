package com.infrastructure.role.flow.intermediaries;

/**
 * 中介者
 * @param <S>
 * @param <T>
 */
public interface Intermediaries<S, T> {


    void coordination(S source, T target);
}
