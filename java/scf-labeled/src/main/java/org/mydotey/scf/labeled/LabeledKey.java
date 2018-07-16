package org.mydotey.scf.labeled;

/**
 * @author koqizhao
 *
 * Jun 19, 2018
 */
public interface LabeledKey<K> {

    /**
     * non-null
     */
    K getKey();

    /**
     * default to null
     */
    PropertyLabels getLabels();

    public interface Builder<K> extends AbstractBuilder<K, Builder<K>> {

    }

    public interface AbstractBuilder<K, B extends AbstractBuilder<K, B>> {

        /**
         * required
         * @see LabeledKey#getKey()
         */
        B setKey(K key);

        /**
         * optional
         * @see LabeledKey#getLabels()
         */
        B setPropertyLabels(PropertyLabels propertyLabels);

        LabeledKey<K> build();

    }

}
