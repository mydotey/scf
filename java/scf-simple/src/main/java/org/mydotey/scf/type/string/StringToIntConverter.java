package org.mydotey.scf.type.string;

/**
 * @author koqizhao
 *
 * May 21, 2018
 */
public class StringToIntConverter extends StringConverter<Integer> {

    public static StringToIntConverter DEFAULT = new StringToIntConverter();

    public StringToIntConverter() {
        super(Integer.class);
    }

    @Override
    public Integer convert(String source) {
        return Integer.parseInt(source);
    }

}
