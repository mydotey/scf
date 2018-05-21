package org.mydotey.scf.type;

/**
 * @author koqizhao
 *
 * May 21, 2018
 */
public class InplaceConverter<V> extends AbstractTypeConverter<V, V> {

    public InplaceConverter(Class<V> sourceType) {
        super(sourceType, sourceType);
    }

    @Override
    public V convert(V s) {
        return s;
    }

}
