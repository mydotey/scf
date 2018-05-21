package org.mydotey.scf.type;

/**
 * @author koqizhao
 *
 * May 21, 2018
 */
public interface TypeConverter<S, T> {

    Class<S> getSourceType();

    Class<T> getTargetType();

    T convert(S source);

}
