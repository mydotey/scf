package org.mydotey.scf;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author koqizhao
 *
 * May 17, 2018
 */
public class DefaultConfigurationManagerConfig implements ConfigurationManagerConfig, Cloneable {

    private String _name;
    private Map<Integer, ConfigurationSource> _sources;
    private Consumer<Runnable> _taskExecutor;

    protected DefaultConfigurationManagerConfig() {

    }

    @Override
    public String getName() {
        return _name;
    }

    @Override
    public Map<Integer, ConfigurationSource> getSources() {
        return _sources;
    }

    @Override
    public Consumer<Runnable> getTaskExecutor() {
        return _taskExecutor;
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
            copy._sources = Collections.unmodifiableMap(new HashMap<>(_sources));
        return copy;
    }

    @Override
    public String toString() {
        return String.format("%s { name: %s, taskExecutor: %s, sources: %s }", getClass().getSimpleName(), _name,
                _taskExecutor, _sources);
    }

    public static class Builder
            extends DefaultAbstractBuilder<ConfigurationManagerConfig.Builder, ConfigurationManagerConfig>
            implements ConfigurationManagerConfig.Builder {

    }

    @SuppressWarnings("unchecked")
    public static abstract class DefaultAbstractBuilder<B extends ConfigurationManagerConfig.AbstractBuilder<B, C>, C extends ConfigurationManagerConfig>
            implements ConfigurationManagerConfig.AbstractBuilder<B, C> {

        protected static final Consumer<Runnable> DEFAULT_TASK_EXECUTOR = t -> t.run();

        private DefaultConfigurationManagerConfig _config;

        protected DefaultAbstractBuilder() {
            _config = (DefaultConfigurationManagerConfig) newConfig();
        }

        protected C newConfig() {
            return (C) new DefaultConfigurationManagerConfig();
        }

        protected C getConfig() {
            return (C) _config;
        }

        @Override
        public B setName(String name) {
            _config._name = name;
            return (B) this;
        }

        @Override
        public B addSource(int priority, ConfigurationSource source) {
            if (source != null) {
                if (_config._sources == null)
                    _config._sources = new HashMap<>();
                else {
                    ConfigurationSource existed = _config._sources.get(priority);
                    if (existed != null)
                        throw new IllegalArgumentException(String.format(
                                "duplicate source priority, existing: { priority: %d, source: %s }, new: { priority: %d, source: %s }",
                                priority, existed, priority, source));
                }
                _config._sources.put(priority, source);
            }

            return (B) this;
        }

        @Override
        public B addSources(Map<Integer, ConfigurationSource> sources) {
            if (sources != null)
                sources.forEach(this::addSource);

            return (B) this;
        }

        @Override
        public B setTaskExecutor(Consumer<Runnable> taskExecutor) {
            _config._taskExecutor = taskExecutor;
            return (B) this;
        }

        @Override
        public C build() {
            if (_config._name == null || _config._name.trim().isEmpty())
                throw new IllegalArgumentException("name is null or empty");
            _config._name = _config._name.trim();

            if (_config._sources == null || _config._sources.isEmpty())
                throw new IllegalArgumentException("sources is null or empty");

            if (_config._taskExecutor == null)
                _config._taskExecutor = DEFAULT_TASK_EXECUTOR;

            return (C) _config.clone();
        }

    }

}
