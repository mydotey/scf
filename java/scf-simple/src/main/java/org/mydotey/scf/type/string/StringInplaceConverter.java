package org.mydotey.scf.type.string;

import org.mydotey.scf.type.InplaceConverter;

/**
 * @author koqizhao
 *
 * May 21, 2018
 */
public class StringInplaceConverter extends InplaceConverter<String> {

    public static final StringInplaceConverter DEFAULT = new StringInplaceConverter();

    public StringInplaceConverter() {
        super(String.class);
    }

}
