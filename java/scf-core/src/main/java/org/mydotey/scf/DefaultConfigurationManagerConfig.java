package org.mydotey.scf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author koqizhao
 *
 * May 17, 2018
 */
public class DefaultConfigurationManagerConfig implements ConfigurationManagerConfig, Cloneable {

    private String _name;
    private List<ConfigurationSource> _sources;
    private int _changeHandlerThreadPoolSize;

    protected DefaultConfigurationManagerConfig() {

    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public Collection<ConfigurationSource> getSources() {
        return _sources;
    }

    @Override
    public int getChangeHandlerThreadPoolSize() {
        return _changeHandlerThreadPoolSize;
    }

    @Override
    public DefaultConfigurationManagerConfig clone() {
        DefaultConfigurationManagerConfig copy = null;
        try {
            copy = (DefaultConfigurationManagerConfig) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        if (_sources != null)
            copy._sources = Collections.unmodifiableList(new ArrayList<>(_sources));
        return copy;
    }

    public static class Builder extends DefaultAbstractBuilder<ConfigurationManagerConfig.Builder>
            implements ConfigurationManagerConfig.Builder {

    }

    @SuppressWarnings("unchecked")
    public static abstract class DefaultAbstractBuilder<B extends ConfigurationManagerConfig.AbstractBuilder<B>>
            implements ConfigurationManagerConfig.AbstractBuilder<B> {

        private DefaultConfigurationManagerConfig _config;

        protected DefaultAbstractBuilder() {
            _config = newConfig();
            _config._changeHandlerThreadPoolSize = 1;
        }

        protected DefaultConfigurationManagerConfig newConfig() {
            return new DefaultConfigurationManagerConfig();
        }

        protected DefaultConfigurationManagerConfig getCofnig() {
            return _config;
        }

        @Override
        public B setName(String name) {
            _config._name = name;
            return (B) this;
        }

        @Override
        public B setSources(Collection<ConfigurationSource> sources) {
            if (sources == null)
                _config._sources = null;
            else {
                if (_config._sources == null)
                    _config._sources = new ArrayList<>();
                else
                    _config._sources.clear();
                sources.forEach(s -> {
                    if (s != null)
                        _config._sources.add(s);
                });
            }

            return (B) this;
        }

        @Override
        public B setChangeHandlerThreadPoolSize(int changeHandlerThreadPoolSize) {
            _config._changeHandlerThreadPoolSize = changeHandlerThreadPoolSize;
            return (B) this;
        }

        @Override
        public DefaultConfigurationManagerConfig build() {
            if (_config._name == null)
                throw new IllegalArgumentException("key is null");

            _config._name = _config._name.trim();
            if (_config._name.isEmpty())
                throw new IllegalArgumentException("key is empty");

            if (_config._sources == null || _config._sources.isEmpty())
                throw new IllegalArgumentException("sources is null or empty");

            if (_config._changeHandlerThreadPoolSize < 0)
                throw new IllegalArgumentException("changeHandlerThreadPoolSize is less than 0");

            return _config.clone();
        }

    }

}
