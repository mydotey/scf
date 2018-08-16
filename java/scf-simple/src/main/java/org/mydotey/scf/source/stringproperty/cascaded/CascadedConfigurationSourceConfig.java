package org.mydotey.scf.source.stringproperty.cascaded;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.mydotey.scf.ConfigurationSourceConfig;
import org.mydotey.scf.DefaultConfigurationSourceConfig;
import org.mydotey.scf.source.stringproperty.StringPropertyConfigurationSource;

/**
 * @author koqizhao
 *
 * May 17, 2018
 */
public class CascadedConfigurationSourceConfig<C extends ConfigurationSourceConfig>
        extends DefaultConfigurationSourceConfig {

    private String _keySeparator;
    private List<String> _cascadedFactors;

    private StringPropertyConfigurationSource<C> _source;

    protected CascadedConfigurationSourceConfig() {

    }

    public String getKeySeparator() {
        return _keySeparator;
    }

    public List<String> getCascadedFactors() {
        return _cascadedFactors;
    }

    public StringPropertyConfigurationSource<C> getSource() {
        return _source;
    }

    @SuppressWarnings("unchecked")
    @Override
    public CascadedConfigurationSourceConfig<C> clone() {
        CascadedConfigurationSourceConfig<C> copy = (CascadedConfigurationSourceConfig<C>) super.clone();

        if (_cascadedFactors != null)
            copy._cascadedFactors = Collections.unmodifiableList(new ArrayList<>(_cascadedFactors));

        return copy;
    }

    @Override
    public String toString() {
        return String.format("%s { name: %s, keySeparator: %s, cascadedFactors: %s }", getClass().getSimpleName(),
                getName(), _keySeparator, _cascadedFactors);
    }

    public static class Builder<C extends ConfigurationSourceConfig> extends
            DefaultConfigurationSourceConfig.DefaultAbstractBuilder<Builder<C>, CascadedConfigurationSourceConfig<C>> {

        @Override
        protected CascadedConfigurationSourceConfig<C> newConfig() {
            return new CascadedConfigurationSourceConfig<>();
        }

        public Builder<C> setKeySeparator(String keySeparator) {
            getConfig()._keySeparator = keySeparator;
            return this;
        }

        public Builder<C> addCascadedFactor(String cascadedFactor) {
            if (cascadedFactor == null)
                return this;

            cascadedFactor = cascadedFactor.trim();
            if (cascadedFactor.isEmpty())
                return this;

            if (getConfig()._cascadedFactors == null)
                getConfig()._cascadedFactors = new ArrayList<>();
            getConfig()._cascadedFactors.add(cascadedFactor);

            return this;
        }

        public Builder<C> setSource(StringPropertyConfigurationSource<C> source) {
            getConfig()._source = source;
            return this;
        }

        public Builder<C> addCascadedFactors(List<String> cascadedFactors) {
            if (cascadedFactors != null)
                cascadedFactors.forEach(this::addCascadedFactor);

            return this;
        }

        @Override
        public CascadedConfigurationSourceConfig<C> build() {
            Objects.requireNonNull(getConfig()._keySeparator, "keySeparator is null");
            getConfig()._keySeparator = getConfig().getKeySeparator().trim();
            if (getConfig().getKeySeparator().isEmpty())
                throw new IllegalArgumentException("keySeparator is empty");

            Objects.requireNonNull(getConfig()._cascadedFactors, "cascadedFactors is null");
            if (getConfig().getCascadedFactors().isEmpty())
                throw new IllegalArgumentException("cascadedFactors is empty");

            Objects.requireNonNull(getConfig()._source, "source is null");

            return (CascadedConfigurationSourceConfig<C>) super.build();
        }
    }

}
