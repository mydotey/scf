package org.mydotey.scf.type.string;

/**
 * @author koqizhao
 *
 * May 21, 2018
 */
public class StringToDoubleConverter extends StringConverter<Double> {

    public static final StringToDoubleConverter DEFAULT = new StringToDoubleConverter();

    public StringToDoubleConverter() {
        super(Double.class);
    }

    @Override
    public Double convert(String source) {
        return Double.parseDouble(source);
    }

}
