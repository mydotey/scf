package org.mydotey.scf;

import java.util.Collection;
import java.util.Comparator;
import java.util.function.Function;

import org.mydotey.scf.type.TypeConverter;

/**
 * @author koqizhao
 *
 * May 17, 2018
 */
@SuppressWarnings("rawtypes")
public interface PropertyConfig<K, V> {

    /**
     * a unique key in a configuration manager to identify a unique property,
     * <p>
     * non-null, non-empty
     */
    K getKey();

    /**
     * type of the property value,
     * <p>
     * non-null
     */
    Class<V> getValueType();

    /**
     * default value of the property
     * <p>
     * default to null
     */
    V getDefaultValue();

    /**
     * type converters used to convert values of different types,
     * for example, some configuration source has string value,
     * but integer is needed, so it's necessary to provide a string-to-int converter
     * <p>
     * default to null
     */
    Collection<TypeConverter> getValueConverters();

    /**
     * a chance for the user to check the value before using a property value provided by a configuration source,
     * filter input is non-null, if output is null, the value will be ignored,
     * if output is non-null, output will be used as the property value
     * <p>
     * default to null
     */
    Function<V, V> getValueFilter();

    /**
     * if a value type is not comparable by the equals method, can give a comparator instead
     * <p>
     * default to null
     */
    Comparator<V> getValueComparator();

    public interface Builder<K, V> extends AbstractBuilder<K, V, Builder<K, V>, PropertyConfig<K, V>> {

    }

    public interface AbstractBuilder<K, V, B extends AbstractBuilder<K, V, B, C>, C extends PropertyConfig<K, V>> {

        /**
         * required
         * <p>
         * @see PropertyConfig#getKey()
         */
        B setKey(K key);

        /**
         * required
         * <p>
         * @see PropertyConfig#getValueType()
         */
        B setValueType(Class<V> valueType);

        /**
         * optional
         * <p>
         * @see PropertyConfig#getDefaultValue()
         */
        B setDefaultValue(V value);

        /**
         * optional
         * <p>
         * @see PropertyConfig#getValueConverters()
         */
        B addValueConverter(TypeConverter valueConverter);

        /**
         * optional
         * <p>
         * @see PropertyConfig#getValueConverters()
         */
        B addValueConverters(Collection<TypeConverter> valueConverters);

        /**
         * optional
         * <p>
         * @see PropertyConfig#getValueFilter()
         */
        B setValueFilter(Function<V, V> valueFilter);

        /**
         * optional
         * <p>
         * @see PropertyConfig#getValueComparator()
         */
        B setValueComparator(Comparator<V> valueComparator);

        C build();
    }

}
