package org.mydotey.scf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import org.mydotey.scf.type.TypeConverter;

/**
 * @author koqizhao
 *
 * May 17, 2018
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class DefaultPropertyConfig<K, V> implements PropertyConfig<K, V>, Cloneable {

    private K _key;
    private Class<V> _valueType;
    private V _defaultValue;
    private List<TypeConverter> _valueConverters;
    private Function<V, V> _valueFilter;

    private volatile int _hashCode;

    protected DefaultPropertyConfig() {

    }

    @Override
    public K getKey() {
        return _key;
    }

    @Override
    public Class<V> getValueType() {
        return _valueType;
    }

    @Override
    public V getDefaultValue() {
        return _defaultValue;
    }

    @Override
    public Collection<TypeConverter> getValueConverters() {
        return _valueConverters;
    }

    @Override
    public Function<V, V> getValueFilter() {
        return _valueFilter;
    }

    @Override
    public DefaultPropertyConfig<K, V> clone() {
        DefaultPropertyConfig<K, V> copy = null;
        try {
            copy = (DefaultPropertyConfig<K, V>) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        if (_valueConverters != null)
            copy._valueConverters = Collections.unmodifiableList(new ArrayList<>(_valueConverters));
        return copy;
    }

    @Override
    public String toString() {
        return String.format("%s { key: %s, valueType: %s, defaultValue: %s, valueConverters: %s, valueFilter: %s }",
                getClass().getSimpleName(), _key, _valueType, _defaultValue, _valueConverters, _valueFilter);
    }

    @Override
    public int hashCode() {
        if (_hashCode == 0) {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((_defaultValue == null) ? 0 : _defaultValue.hashCode());
            result = prime * result + ((_key == null) ? 0 : _key.hashCode());
            result = prime * result + ((_valueConverters == null) ? 0 : _valueConverters.hashCode());
            result = prime * result + ((_valueFilter == null) ? 0 : _valueFilter.hashCode());
            result = prime * result + ((_valueType == null) ? 0 : _valueType.hashCode());
            _hashCode = result;
        }

        return _hashCode;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;

        if (other == null)
            return false;

        if (getClass() != other.getClass())
            return false;

        if (hashCode() != other.hashCode())
            return false;

        DefaultPropertyConfig<K, V> propertyConfig = (DefaultPropertyConfig<K, V>) other;

        if (!Objects.equals(_key, propertyConfig._key))
            return false;

        if (!Objects.equals(_valueType, propertyConfig._valueType))
            return false;

        if (!Objects.equals(_defaultValue, propertyConfig._defaultValue))
            return false;

        if (!Objects.equals(_valueConverters, propertyConfig._valueConverters))
            return false;

        if (!Objects.equals(_valueFilter, propertyConfig._valueFilter))
            return false;

        return true;
    }

    public static class Builder<K, V>
            extends DefaultAbstractBuilder<K, V, PropertyConfig.Builder<K, V>, PropertyConfig<K, V>>
            implements PropertyConfig.Builder<K, V> {

    }

    public static abstract class DefaultAbstractBuilder<K, V, B extends PropertyConfig.AbstractBuilder<K, V, B, C>, C extends PropertyConfig<K, V>>
            implements PropertyConfig.AbstractBuilder<K, V, B, C> {

        private DefaultPropertyConfig<K, V> _config;

        protected DefaultAbstractBuilder() {
            _config = (DefaultPropertyConfig<K, V>) newConfig();
        }

        protected C newConfig() {
            return (C) new DefaultPropertyConfig<>();
        }

        protected C getConfig() {
            return (C) _config;
        }

        @Override
        public B setKey(K key) {
            _config._key = key;
            return (B) this;
        }

        @Override
        public B setValueType(Class<V> valueType) {
            _config._valueType = valueType;
            return (B) this;
        }

        @Override
        public B setDefaultValue(V value) {
            _config._defaultValue = value;
            return (B) this;
        }

        @Override
        public B addValueConverter(TypeConverter valueConverter) {
            if (valueConverter != null) {
                if (_config._valueConverters == null)
                    _config._valueConverters = new ArrayList<>();
                _config._valueConverters.add(valueConverter);
            }

            return (B) this;
        }

        @Override
        public B addValueConverters(Collection<TypeConverter> valueConverters) {
            if (valueConverters != null)
                valueConverters.forEach(this::addValueConverter);

            return (B) this;
        }

        @Override
        public B setValueFilter(Function<V, V> valueFilter) {
            _config._valueFilter = valueFilter;
            return (B) this;
        }

        @Override
        public C build() {
            Objects.requireNonNull(_config._key, "key is null");
            Objects.requireNonNull(_config._valueType, "valueType is null");

            return (C) _config.clone();
        }
    }
}
