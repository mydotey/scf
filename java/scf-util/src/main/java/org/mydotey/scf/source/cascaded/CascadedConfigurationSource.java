package org.mydotey.scf.source.cascaded;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.mydotey.scf.source.stringproperties.StringPropertiesConfigurationSource;

/**
 * Created by Qiang Zhao on 10/05/2016.
 */
public class CascadedConfigurationSource extends StringPropertiesConfigurationSource {

    private StringPropertiesConfigurationSource _source;
    private List<String> _cascadedKeyParts;

    public CascadedConfigurationSource(CascadedConfigurationSourceConfig config,
            StringPropertiesConfigurationSource source) {
        super(config);

        Objects.requireNonNull(source, "source is null");
        _source = source;

        setDynamic(_source.isDynamic());
        if (isDynamic())
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
            String value = _source.getPropertyValue(key + keyPart);
            if (value != null)
                return value;
        }

        return null;
    }

}
