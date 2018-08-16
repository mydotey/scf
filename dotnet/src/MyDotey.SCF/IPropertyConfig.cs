using System;
using System.Collections.Generic;
using sType = System.Type;

using MyDotey.SCF.Type;
using MyDotey.SCF.Filter;

namespace MyDotey.SCF
{
    public interface IPropertyConfig
    {
        object Key { get; }
        sType ValueType { get; }
        object DefaultValue { get; }
        ICollection<ITypeConverter> ValueConverters { get; }
        IValueFilter ValueFilter { get; }
    }

    /**
     * @author koqizhao
     *
     * May 17, 2018
     */
    public abstract class PropertyConfig<K, V> : IPropertyConfig
    {
        object IPropertyConfig.Key
        {
            get { return Key; }
        }

        /**
         * a unique key in a configuration manager to identify a unique property,
         * <p>
         * non-null, non-empty
         */
        public abstract K Key { get; }

        sType IPropertyConfig.ValueType { get { return typeof(V); } }

        object IPropertyConfig.DefaultValue { get { return DefaultValue; } }

        /**
         * default value of the property
         * <p>
         * default to null
         */
        public abstract V DefaultValue { get; }

        /**
         * type converters used to convert values of different types,
         * for example, some configuration source has string value,
         * but integer is needed, so it's necessary to provide a string-to-int converter
         * <p>
         * default to null
         */
        public abstract ICollection<ITypeConverter> ValueConverters { get; }

        IValueFilter IPropertyConfig.ValueFilter { get { return ValueFilter; } }

        /**
         * a chance for the user to check the value before using a property value provided by a configuration source,
         * filter input is non-null, if output is null, the value will be ignored,
         * if output is non-null, output will be used as the property value
         * <p>
         * default to null
         */
        public abstract IValueFilter<V> ValueFilter { get; }

        public interface IBuilder : IAbstractBuilder<IBuilder, PropertyConfig<K, V>>
        {

        }

        public interface IAbstractBuilder<B, C>
            where B : IAbstractBuilder<B, C>
            where C : PropertyConfig<K, V>
        {
            /**
             * required
             * <p>
             * @see PropertyConfig#getKey()
             */
            B SetKey(K key);

            /**
             * optional
             * <p>
             * @see PropertyConfig#getDefaultValue()
             */
            B SetDefaultValue(V value);

            /**
             * optional
             * <p>
             * @see PropertyConfig#getValueConverters()
             */
            B AddValueConverter(ITypeConverter valueConverter);

            /**
             * optional
             * <p>
             * @see PropertyConfig#getValueConverters()
             */
            B AddValueConverters(ICollection<ITypeConverter> valueConverters);

            /**
             * optional
             * <p>
             * @see PropertyConfig#getValueFilter()
             */
            B SetValueFilter(IValueFilter<V> valueFilter);

            B SetValueFilter(Func<V, V> valueFilter);

            C Build();
        }
    }
}