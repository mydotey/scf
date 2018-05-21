package org.mydotey.scf.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import org.mydotey.scf.PropertyConfig;
import org.mydotey.scf.type.TypeConverter;

/**
 * @author koqizhao
 *
 * May 17, 2018
 */
@SuppressWarnings("rawtypes")
public class DefaultPropertyConfig<K, V> implements PropertyConfig<K, V>, Cloneable {

    private K _key;
    private Class<V> _valueType;
    private V _defaultValue;
    private List<TypeConverter> _valueConverters;
    private List<Function<V, V>> _valueFilters;

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
    public Collection<Function<V, V>> getValueFilters() {
        return _valueFilters;
    }

    @SuppressWarnings("unchecked")
    @Override
    public DefaultPropertyConfig<K, V> clone() {
        DefaultPropertyConfig<K, V> copy = null;
        try {
            copy = (DefaultPropertyConfig<K, V>) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        if (_valueFilters != null)
            copy._valueFilters = Collections.unmodifiableList(new ArrayList<>(_valueFilters));
        return copy;
    }

    @Override
    public String toString() {
        return String.format("{ key: %s, valueType: %s, defaultValue: %s, valueFilters: %s }", _key, _valueType,
                _defaultValue, _valueFilters);
    }

    public static class Builder<K, V> extends DefaultAbstractBuilder<K, V, PropertyConfig.Builder<K, V>>
            implements PropertyConfig.Builder<K, V> {

    }

    @SuppressWarnings("unchecked")
    public static abstract class DefaultAbstractBuilder<K, V, B extends PropertyConfig.AbstractBuilder<K, V, B>>
            implements PropertyConfig.AbstractBuilder<K, V, B> {

        private DefaultPropertyConfig<K, V> _config;

        protected DefaultAbstractBuilder() {
            _config = newConfig();
        }

        protected DefaultPropertyConfig<K, V> newConfig() {
            return new DefaultPropertyConfig<>();
        }

        protected DefaultPropertyConfig<K, V> getConfig() {
            return _config;
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
        public B setValueConverters(Collection<TypeConverter> valueConverters) {
            if (valueConverters == null)
                _config._valueConverters = null;
            else {
                if (_config._valueConverters == null)
                    _config._valueConverters = new ArrayList<>();
                else
                    _config._valueConverters.clear();
                valueConverters.forEach(f -> {
                    if (f != null)
                        _config._valueConverters.add(f);
                });
            }

            return (B) this;
        }

        @Override
        public B setValueFilters(Collection<Function<V, V>> valueFilters) {
            if (valueFilters == null)
                _config._valueFilters = null;
            else {
                if (_config._valueFilters == null)
                    _config._valueFilters = new ArrayList<>();
                else
                    _config._valueFilters.clear();
                valueFilters.forEach(f -> {
                    if (f != null)
                        _config._valueFilters.add(f);
                });
            }

            return (B) this;
        }

        @Override
        public DefaultPropertyConfig<K, V> build() {
            Objects.requireNonNull(_config._key, "key is null");
            Objects.requireNonNull(_config._valueType, "valueType is null");

            return _config.clone();
        }

    }

}
