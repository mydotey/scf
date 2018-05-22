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

    @Override
    public String toString() {
        return String.format("{ name: %s, priority: %s }", _name, _priority);
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

        protected DefaultConfigurationSourceConfig getConfig() {
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
            if (_config._name == null || _config._name.trim().isEmpty())
                throw new IllegalArgumentException("name is null or empty");
            _config._name = _config._name.trim();

            return _config.clone();
        }

    }

}
