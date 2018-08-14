using System;
using System.Collections.Generic;
using System.Linq;

using MyDotey.SCF.Type;
using MyDotey.SCF.Filter;

namespace MyDotey.SCF
{
    /**
     * @author koqizhao
     *
     * May 17, 2018
     */
    public class DefaultPropertyConfig<K, V> : PropertyConfig<K, V>, ICloneable
    {
        private K _key;
        private V _defaultValue;
        private List<TypeConverter> _valueConverters;
        private ValueFilter<V> _valueFilter;

        private volatile int _hashCode;

        protected DefaultPropertyConfig()
        {

        }

        public override K getKey()
        {
            return _key;
        }

        public override V getDefaultValue()
        {
            return _defaultValue;
        }

        public override ICollection<TypeConverter> getValueConverters()
        {
            return _valueConverters;
        }

        public override ValueFilter<V> getValueFilter()
        {
            return _valueFilter;
        }

        public virtual object Clone()
        {
            DefaultPropertyConfig<K, V> copy = (DefaultPropertyConfig<K, V>)MemberwiseClone();
            if (_valueConverters != null)
                copy._valueConverters = new List<TypeConverter>(_valueConverters);
            return copy;
        }

        public override string ToString()
        {
            return string.Format("{0} {{ key: {1}, valueType: {2}, defaultValue: {3}, valueConverters: [ {4} ], valueFilter: {5} }}",
                    GetType().Name, _key, typeof(V), _defaultValue,
                    _valueConverters == null ? null : string.Join(", ", _valueConverters), _valueFilter);
        }

        public override int GetHashCode()
        {
            if (_hashCode == 0)
            {
                int prime = 31;
                int result = 1;
                result = prime * result + ((_defaultValue == null) ? 0 : _defaultValue.GetHashCode());
                result = prime * result + ((_key == null) ? 0 : _key.GetHashCode());
                result = prime * result + ((_valueConverters == null) ? 0 : _valueConverters.HashCode());
                result = prime * result + ((_valueFilter == null) ? 0 : _valueFilter.GetHashCode());
                result = prime * result + typeof(V).GetHashCode();
                _hashCode = result;
            }

            return _hashCode;
        }

        public override bool Equals(object other)
        {
            if (object.ReferenceEquals(this, other))
                return true;

            if (other == null)
                return false;

            if (GetType() != other.GetType())
                return false;

            if (GetHashCode() != other.GetHashCode())
                return false;

            DefaultPropertyConfig<K, V> propertyConfig = (DefaultPropertyConfig<K, V>)other;

            if (!object.Equals(_key, propertyConfig._key))
                return false;

            if (!object.Equals(_defaultValue, propertyConfig._defaultValue))
                return false;

            if (!_valueConverters.Equal(propertyConfig._valueConverters))
                return false;

            if (!object.Equals(_valueFilter, propertyConfig._valueFilter))
                return false;

            return true;
        }

        public new class Builder : DefaultAbstractBuilder<PropertyConfig<K, V>.Builder, PropertyConfig<K, V>>
            , PropertyConfig<K, V>.Builder
        {

        }

        public abstract class DefaultAbstractBuilder<B, C>
                : PropertyConfig<K, V>.AbstractBuilder<B, C>
                where B : PropertyConfig<K, V>.AbstractBuilder<B, C>
                where C : PropertyConfig<K, V>
        {
            private DefaultPropertyConfig<K, V> _config;

            protected DefaultAbstractBuilder()
            {
                _config = (DefaultPropertyConfig<K, V>)(object)newConfig();
            }

            protected virtual C newConfig()
            {
                return (C)(object)(new DefaultPropertyConfig<K, V>());
            }

            protected virtual C getConfig()
            {
                return (C)(object)_config;
            }

            public virtual B setKey(K key)
            {
                _config._key = key;
                return (B)(object)this;
            }

            public virtual B setDefaultValue(V value)
            {
                _config._defaultValue = value;
                return (B)(object)this;
            }

            public virtual B addValueConverter(TypeConverter valueConverter)
            {
                if (valueConverter != null)
                {
                    if (_config._valueConverters == null)
                        _config._valueConverters = new List<TypeConverter>();
                    _config._valueConverters.Add(valueConverter);
                }

                return (B)(object)this;
            }

            public virtual B addValueConverters(ICollection<TypeConverter> valueConverters)
            {
                if (valueConverters != null)
                    valueConverters.ToList().ForEach(c => addValueConverter(c));

                return (B)(object)this;
            }

            public virtual B setValueFilter(ValueFilter<V> valueFilter)
            {
                _config._valueFilter = valueFilter;
                return (B)(object)this;
            }

            public virtual B setValueFilter(Func<V, V> valueFilter)
            {
                _config._valueFilter = valueFilter == null ? null : new DefaultValueFilter<V>(valueFilter);
                return (B)(object)this;
            }

            public virtual C build()
            {
                if (_config._key == null)
                    throw new ArgumentNullException("key is null");

                return (C)_config.Clone();
            }
        }
    }
}