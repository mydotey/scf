package org.mydotey.scf.type;

/**
 * @author koqizhao
 *
 * May 21, 2018
 * 
 * convert a value from source type to target type
 */
public interface TypeConverter<S, T> {

    Class<S> getSourceType();

    Class<T> getTargetType();

    T convert(S source);

}
