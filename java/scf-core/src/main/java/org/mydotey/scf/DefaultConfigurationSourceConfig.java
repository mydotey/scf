package org.mydotey.scf;

/**
 * @author koqizhao
 *
 * May 17, 2018
 */
public class DefaultConfigurationSourceConfig implements ConfigurationSourceConfig, Cloneable {

    private String _name;
    private int _priority;

    protected DefaultConfigurationSourceConfig() {

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
    public DefaultConfigurationSourceConfig clone() {
        DefaultConfigurationSourceConfig copy = null;
        try {
            copy = (DefaultConfigurationSourceConfig) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        return copy;
    }

    public static class Builder extends DefaultAbstractBuilder<ConfigurationSourceConfig.Builder>
            implements ConfigurationSourceConfig.Builder {

    }

    @SuppressWarnings("unchecked")
    public static abstract class DefaultAbstractBuilder<B extends ConfigurationSourceConfig.AbstractBuilder<B>>
            implements ConfigurationSourceConfig.AbstractBuilder<B> {

        private DefaultConfigurationSourceConfig _config;

        protected DefaultAbstractBuilder() {
            _config = newConfig();
        }

        protected DefaultConfigurationSourceConfig newConfig() {
            return new DefaultConfigurationSourceConfig();
        }

        protected DefaultConfigurationSourceConfig getCofnig() {
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
        public DefaultConfigurationSourceConfig build() {
            if (_config._name == null)
                throw new IllegalArgumentException("key is null");

            _config._name = _config._name.trim();
            if (_config._name.isEmpty())
                throw new IllegalArgumentException("key is empty");

            return _config.clone();
        }

    }

}
