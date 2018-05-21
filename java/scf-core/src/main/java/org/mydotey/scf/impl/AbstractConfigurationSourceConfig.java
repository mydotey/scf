package org.mydotey.scf.impl;

import org.mydotey.scf.ConfigurationSourceConfig;

/**
 * @author koqizhao
 *
 * May 17, 2018
 */
public abstract class AbstractConfigurationSourceConfig implements ConfigurationSourceConfig, Cloneable {

    private String _name;
    private int _priority;

    protected AbstractConfigurationSourceConfig() {

    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public int getPriority() {
        return _priority;
    }

    @Override
    public AbstractConfigurationSourceConfig clone() {
        AbstractConfigurationSourceConfig copy = null;
        try {
            copy = (AbstractConfigurationSourceConfig) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        return copy;
    }

    @Override
    public String toString() {
        return String.format("{ name: %s, priority: %s }", _name, _priority);
    }

    @SuppressWarnings("unchecked")
    public static abstract class AbstractBuilder<B extends ConfigurationSourceConfig.Builder<B>>
            implements ConfigurationSourceConfig.Builder<B> {

        private AbstractConfigurationSourceConfig _config;

        protected AbstractBuilder() {
            _config = newConfig();
        }

        protected abstract AbstractConfigurationSourceConfig newConfig();

        protected AbstractConfigurationSourceConfig getConfig() {
            return _config;
        }

        @Override
        public B setName(String name) {
            _config._name = name;
            return (B) this;
        }

        @Override
        public B setPriority(int priority) {
            _config._priority = priority;
            return (B) this;
        }

        @Override
        public AbstractConfigurationSourceConfig build() {
            if (_config._name == null || _config._name.trim().isEmpty())
                throw new IllegalArgumentException("name is null or empty");
            _config._name = _config._name.trim();

            return _config.clone();
        }

    }

}
