package org.mydotey.scf.source.stringproperty.cascaded;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.mydotey.scf.source.stringproperty.StringPropertyConfigurationSource;

/**
 * @author koqizhao
 *
 * May 17, 2018
 * 
 * allow casaded config like:
 *  key
 *  key.a
 *  key.a.b
 * priority:
 *  key.a.b > k.a > key
 */
public class CascadedConfigurationSource extends StringPropertyConfigurationSource {

    private StringPropertyConfigurationSource _source;
    private List<String> _cascadedKeyParts;

    public CascadedConfigurationSource(CascadedConfigurationSourceConfig config,
            StringPropertyConfigurationSource source) {
        super(config);

        Objects.requireNonNull(source, "source is null");

        _source = source;
        _source.addChangeListener(s -> CascadedConfigurationSource.this.raiseChangeEvent());

        init();
    }

    @Override
    public CascadedConfigurationSourceConfig getConfig() {
        return (CascadedConfigurationSourceConfig) super.getConfig();
    }

    protected void init() {
        _cascadedKeyParts = new ArrayList<>();

        StringBuffer keyPart = new StringBuffer("");
        _cascadedKeyParts.add(keyPart.toString());
        for (String factor : getConfig().getCascadedFactors()) {
            keyPart.append(getConfig().getKeySeparator()).append(factor);
            _cascadedKeyParts.add(keyPart.toString());
        }

        Collections.reverse(_cascadedKeyParts);
        _cascadedKeyParts = Collections.unmodifiableList(_cascadedKeyParts);
    }

    @Override
    public String getPropertyValue(String key) {
        for (String keyPart : _cascadedKeyParts) {
            String cascadedKey = getKey(key, keyPart);
            String value = _source.getPropertyValue(cascadedKey);
            if (value != null)
                return value;
        }

        return null;
    }

    /**
     * allow user to override
     * if the key count is limited, can cache the key and have less memory use
     */
    protected String getKey(String... keyParts) {
        if (keyParts == null)
            return null;

        StringBuilder stringBuilder = new StringBuilder();
        for (String part : keyParts)
            stringBuilder.append(part);
        return stringBuilder.toString();
    }

}
