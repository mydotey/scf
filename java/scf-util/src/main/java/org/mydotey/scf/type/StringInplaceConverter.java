package org.mydotey.scf.type;

/**
 * @author koqizhao
 *
 * May 21, 2018
 */
public class StringInplaceConverter extends InplaceConverter<String> {

    public static StringInplaceConverter DEFAULT = new StringInplaceConverter();

    public StringInplaceConverter() {
        super(String.class);
    }

}
