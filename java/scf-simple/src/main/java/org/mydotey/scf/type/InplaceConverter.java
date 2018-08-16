package org.mydotey.scf.type;

/**
 * @author koqizhao
 *
 * May 21, 2018
 * 
 * source type and target type is the same type, convert do nothing
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
