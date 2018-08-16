package org.mydotey.scf.source.stringproperty;

import org.mydotey.scf.AbstractConfigurationSource;
import org.mydotey.scf.ConfigurationSourceConfig;

/**
 * @author koqizhao
 *
 * May 17, 2018
 */
public abstract class StringPropertyConfigurationSource<C extends ConfigurationSourceConfig>
        extends AbstractConfigurationSource<C> {

    public StringPropertyConfigurationSource(C config) {
        super(config);
    }

    @Override
    public Object getPropertyValue(Object key) {
        if (key.getClass() != String.class)
            return null;

        return getPropertyValue((String) key);
    }

    public abstract String getPropertyValue(String key);

}
