package org.mydotey.scf.labeled;

/**
 * @author koqizhao
 *
 * Jun 19, 2018
 */
public interface LabeledKey<K> {

    K getKey();

    PropertyLabels getLabels();

    public interface Builder<K> extends AbstractBuilder<K, Builder<K>> {

    }

    public interface AbstractBuilder<K, B extends AbstractBuilder<K, B>> {

        B setKey(K key);

        B setPropertyLabels(PropertyLabels propertyLabels);

        LabeledKey<K> build();

    }

}
