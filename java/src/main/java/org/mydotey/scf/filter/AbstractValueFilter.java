package org.mydotey.scf.filter;

import java.util.function.Function;

/**
 * @author koqizhao
 *
 * May 22, 2018
 */
public abstract class AbstractValueFilter<T> implements Function<T, T> {

    @Override
    public abstract boolean equals(Object obj);

    @Override
    public String toString() {
        return String.format("%s { }", getClass().getSimpleName());
    }

}
