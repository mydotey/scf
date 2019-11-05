package org.mydotey.scf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
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
    private Comparator<V> _valueComparator;
    private boolean _static;
    private boolean _required;
    private String _doc;

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
    public Comparator<V> getValueComparator() {
        return _valueComparator;
    }

    @Override
    public boolean isStatic() {
        return _static;
    }

    @Override
    public boolean isRequired() {
        return _required;
    }

    @Override
    public String getDoc() {
        return _doc;
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
        return String.format("%s { key: %s, valueType: %s, defaultValue: %s, valueConverters: %s, valueFilter: %s, "
                + "valueComparator: %s, static: %s, required: %s, doc: %s }",
                getClass().getSimpleName(), _key, _valueType, _defaultValue, _valueConverters, _valueFilter,
                _valueComparator, _static, _required, _doc);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((_defaultValue == null) ? 0 : _defaultValue.hashCode());
        result = prime * result + ((_doc == null) ? 0 : _doc.hashCode());
        result = prime * result + _hashCode;
        result = prime * result + ((_key == null) ? 0 : _key.hashCode());
        result = prime * result + (_required ? 1231 : 1237);
        result = prime * result + (_static ? 1231 : 1237);
        result = prime * result + ((_valueComparator == null) ? 0 : _valueComparator.hashCode());
        result = prime * result + ((_valueConverters == null) ? 0 : _valueConverters.hashCode());
        result = prime * result + ((_valueFilter == null) ? 0 : _valueFilter.hashCode());
        result = prime * result + ((_valueType == null) ? 0 : _valueType.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DefaultPropertyConfig other = (DefaultPropertyConfig) obj;
        if (_defaultValue == null) {
            if (other._defaultValue != null)
                return false;
        } else if (!_defaultValue.equals(other._defaultValue))
            return false;
        if (_doc == null) {
            if (other._doc != null)
                return false;
        } else if (!_doc.equals(other._doc))
            return false;
        if (_hashCode != other._hashCode)
            return false;
        if (_key == null) {
            if (other._key != null)
                return false;
        } else if (!_key.equals(other._key))
            return false;
        if (_required != other._required)
            return false;
        if (_static != other._static)
            return false;
        if (_valueComparator == null) {
            if (other._valueComparator != null)
                return false;
        } else if (!_valueComparator.equals(other._valueComparator))
            return false;
        if (_valueConverters == null) {
            if (other._valueConverters != null)
                return false;
        } else if (!_valueConverters.equals(other._valueConverters))
            return false;
        if (_valueFilter == null) {
            if (other._valueFilter != null)
                return false;
        } else if (!_valueFilter.equals(other._valueFilter))
            return false;
        if (_valueType == null) {
            if (other._valueType != null)
                return false;
        } else if (!_valueType.equals(other._valueType))
            return false;
        return true;
    }

    public static class Builder<K, V>
            extends DefaultAbstractBuilder<K, V, PropertyConfig.Builder<K, V>, PropertyConfig<K, V>>
            implements PropertyConfig.Builder<K, V> {

    }

    public static abstract class DefaultAbstractBuilder<K, V, B extends PropertyConfig.AbstractBuilder<K, V, B, C>, C extends PropertyConfig<K, V>>
            implements PropertyConfig.AbstractBuilder<K, V, B, C> {

        protected static final Comparator DEFAULT_COMPARATOR = (o1, o2) -> Objects.equals(o1, o2) ? 0 : -1;

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
        public B setValueComparator(Comparator<V> valueComparator) {
            _config._valueComparator = valueComparator;
            return (B) this;
        }

        @Override
        public B setStatic(boolean isStatic) {
            _config._static = isStatic;
            return (B) this;
        }

        @Override
        public B setRequired(boolean required) {
            _config._required = required;
            return (B) this;
        }

        @Override
        public B setDoc(String doc) {
            _config._doc = doc;
            return (B) this;
        }

        @Override
        public C build() {
            Objects.requireNonNull(_config._key, "key is null");
            Objects.requireNonNull(_config._valueType, "valueType is null");

            if (_config._valueComparator == null)
                _config._valueComparator = DEFAULT_COMPARATOR;

            return (C) _config.clone();
        }
    }
}
