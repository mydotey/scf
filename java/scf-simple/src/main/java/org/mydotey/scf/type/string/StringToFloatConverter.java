package org.mydotey.scf.type.string;

/**
 * @author koqizhao
 *
 * May 21, 2018
 */
public class StringToFloatConverter extends StringConverter<Float> {

    public static final StringToFloatConverter DEFAULT = new StringToFloatConverter();

    public StringToFloatConverter() {
        super(Float.class);
    }

    @Override
    public Float convert(String source) {
        return Float.parseFloat(source);
    }

}
