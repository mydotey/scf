package org.mydotey.scf.source.stringproperty.cascaded;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.mydotey.scf.DefaultConfigurationSourceConfig;

/**
 * Created by Qiang Zhao on 10/05/2016.
 */
public class CascadedConfigurationSourceConfig extends DefaultConfigurationSourceConfig {

    private String _keySeparator;
    private List<String> _cascadedFactors;

    protected CascadedConfigurationSourceConfig() {

    }

    public String getKeySeparator() {
        return _keySeparator;
    }

    public List<String> getCascadedFactors() {
        return _cascadedFactors;
    }

    @Override
    public CascadedConfigurationSourceConfig clone() {
        CascadedConfigurationSourceConfig copy = null;
        copy = (CascadedConfigurationSourceConfig) super.clone();

        if (_cascadedFactors != null)
            copy._cascadedFactors = Collections.unmodifiableList(new ArrayList<>(_cascadedFactors));

        return copy;
    }

    @Override
    public String toString() {
        return String.format("{ name: %s, priority: %d, keySeparator: %s, cascadedFactors: %s }", getName(),
                getPriority(), _keySeparator, _cascadedFactors);
    }

    public static class Builder extends DefaultConfigurationSourceConfig.DefaultAbstractBuilder<Builder> {

        @Override
        protected DefaultConfigurationSourceConfig newConfig() {
            return new CascadedConfigurationSourceConfig();
        }

        @Override
        protected CascadedConfigurationSourceConfig getConfig() {
            return (CascadedConfigurationSourceConfig) super.getConfig();
        }

        public Builder setKeySeparator(String keySeparator) {
            getConfig()._keySeparator = keySeparator;
            return this;
        }

        public Builder setCascadedFactors(List<String> cascadedFactors) {
            getConfig()._cascadedFactors = null;
            if (cascadedFactors != null)
                cascadedFactors.forEach(f -> {
                    if (f != null && !f.trim().isEmpty()) {
                        if (getConfig()._cascadedFactors == null)
                            getConfig()._cascadedFactors = new ArrayList<>();
                        getConfig()._cascadedFactors.add(f.trim());
                    }
                });

            return this;
        }

        @Override
        public CascadedConfigurationSourceConfig build() {
            Objects.requireNonNull(getConfig()._keySeparator, "keySeparator is null");
            getConfig()._keySeparator = getConfig().getKeySeparator().trim();
            if (getConfig().getKeySeparator().isEmpty())
                throw new IllegalArgumentException("keySeparator is empty");

            Objects.requireNonNull(getConfig()._cascadedFactors, "cascadedFactors is null");
            if (getConfig().getCascadedFactors().isEmpty())
                throw new IllegalArgumentException("cascadedFactors is empty");

            return (CascadedConfigurationSourceConfig) super.build();
        }
    }

}
