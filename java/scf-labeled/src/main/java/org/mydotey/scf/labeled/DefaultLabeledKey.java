package org.mydotey.scf.labeled;

import java.util.Objects;

/**
 * @author koqizhao
 *
 * May 17, 2018
 */
@SuppressWarnings("unchecked")
public class DefaultLabeledKey<K> implements LabeledKey<K>, Cloneable {

    private K _key;
    private PropertyLabels _labels;

    private volatile int _hashCode;

    protected DefaultLabeledKey() {

    }

    @Override
    public K getKey() {
        return _key;
    }

    @Override
    public PropertyLabels getLabels() {
        return _labels;
    }

    @Override
    public DefaultLabeledKey<K> clone() {
        try {
            return (DefaultLabeledKey<K>) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String toString() {
        return String.format("%s { key: %s, labels: %s }", getClass().getSimpleName(), getKey(), getLabels());
    }

    @Override
    public int hashCode() {
        if (_hashCode == 0) {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((_key == null) ? 0 : _key.hashCode());
            result = prime * result + ((_labels == null) ? 0 : _labels.hashCode());
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

        DefaultLabeledKey<K> labeledKey = (DefaultLabeledKey<K>) other;

        if (!Objects.equals(_key, labeledKey._key))
            return false;

        if (!Objects.equals(_labels, labeledKey._labels))
            return false;

        return true;
    }

    public static class Builder<K> extends DefaultAbstractBuilder<K, LabeledKey.Builder<K>>
            implements LabeledKey.Builder<K> {

    }

    public static abstract class DefaultAbstractBuilder<K, B extends LabeledKey.AbstractBuilder<K, B>>
            implements LabeledKey.AbstractBuilder<K, B> {

        private DefaultLabeledKey<K> _labeledKey;

        protected DefaultAbstractBuilder() {
            _labeledKey = newLabeledKey();
        }

        protected DefaultLabeledKey<K> newLabeledKey() {
            return new DefaultLabeledKey<>();
        }

        protected DefaultLabeledKey<K> getLabeledKey() {
            return _labeledKey;
        }

        @Override
        public B setKey(K key) {
            getLabeledKey()._key = key;
            return (B) this;
        }

        @Override
        public B setPropertyLabels(PropertyLabels labels) {
            getLabeledKey()._labels = labels;
            return (B) this;
        }

        @Override
        public DefaultLabeledKey<K> build() {
            Objects.requireNonNull(getLabeledKey()._key, "key is null");
            Objects.requireNonNull(getLabeledKey()._labels, "labels is null");

            return _labeledKey.clone();
        }

    }

}
