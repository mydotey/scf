using System;
using System.Collections.Generic;
using sType = System.Type;

using MyDotey.SCF.Type;
using MyDotey.SCF.Filter;

namespace MyDotey.SCF
{
    public interface PropertyConfig
    {
        object getKey();
        sType getValueType();
        object getDefaultValue();
        ICollection<TypeConverter> getValueConverters();
        ValueFilter getValueFilter();
    }

    /**
     * @author koqizhao
     *
     * May 17, 2018
     */
    public abstract class PropertyConfig<K, V> : PropertyConfig
    {
        object PropertyConfig.getKey()
        {
            return getKey();
        }

        /**
         * a unique key in a configuration manager to identify a unique property,
         * <p>
         * non-null, non-empty
         */
        public abstract K getKey();

        sType PropertyConfig.getValueType()
        {
            return typeof(V);
        }

        object PropertyConfig.getDefaultValue()
        {
            return getDefaultValue();
        }

        /**
         * default value of the property
         * <p>
         * default to null
         */
        public abstract V getDefaultValue();

        /**
         * type converters used to convert values of different types,
         * for example, some configuration source has string value,
         * but integer is needed, so it's necessary to provide a string-to-int converter
         * <p>
         * default to null
         */
        public abstract ICollection<TypeConverter> getValueConverters();

        ValueFilter PropertyConfig.getValueFilter()
        {
            return getValueFilter();
        }

        /**
         * a chance for the user to check the value before using a property value provided by a configuration source,
         * filter input is non-null, if output is null, the value will be ignored,
         * if output is non-null, output will be used as the property value
         * <p>
         * default to null
         */
        public abstract ValueFilter<V> getValueFilter();

        public interface Builder : AbstractBuilder<Builder, PropertyConfig<K, V>>
        {

        }

        public interface AbstractBuilder<B, C>
            where B : AbstractBuilder<B, C>
            where C : PropertyConfig<K, V>
        {
            /**
             * required
             * <p>
             * @see PropertyConfig#getKey()
             */
            B setKey(K key);

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
            B addValueConverters(ICollection<TypeConverter> valueConverters);

            /**
             * optional
             * <p>
             * @see PropertyConfig#getValueFilter()
             */
            B setValueFilter(ValueFilter<V> valueFilter);

            B setValueFilter(Func<V, V> valueFilter);

            C build();
        }
    }
}