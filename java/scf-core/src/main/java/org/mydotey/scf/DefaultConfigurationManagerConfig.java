package org.mydotey.scf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author koqizhao
 *
 * May 17, 2018
 */
public class DefaultConfigurationManagerConfig implements ConfigurationManagerConfig, Cloneable {

    private String _name;
    private List<ConfigurationSource> _sources;
    private Consumer<Runnable> _taskExecutor;

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
            copy._sources = Collections.unmodifiableList(new ArrayList<>(_sources));
        return copy;
    }

    @Override
    public String toString() {
        return String.format("{ name: %s, taskExecutor: %s, sources: %s }", _name, _taskExecutor, _sources);
    }

    public static class Builder extends DefaultAbstractBuilder<ConfigurationManagerConfig.Builder>
            implements ConfigurationManagerConfig.Builder {

    }

    @SuppressWarnings("unchecked")
    public static abstract class DefaultAbstractBuilder<B extends ConfigurationManagerConfig.AbstractBuilder<B>>
            implements ConfigurationManagerConfig.AbstractBuilder<B> {

        protected static final Consumer<Runnable> DEFAULT_TASK_EXECUTOR = new Consumer<Runnable>() {
            @Override
            public void accept(Runnable t) {
                t.run();
            }
        };

        private DefaultConfigurationManagerConfig _config;

        protected DefaultAbstractBuilder() {
            _config = newConfig();
        }

        protected DefaultConfigurationManagerConfig newConfig() {
            return new DefaultConfigurationManagerConfig();
        }

        protected DefaultConfigurationManagerConfig getConfig() {
            return _config;
        }

        @Override
        public B setName(String name) {
            _config._name = name;
            return (B) this;
        }

        @Override
        public B setSources(Collection<ConfigurationSource> sources) {
            _config._sources = null;
            if (sources != null) {
                sources.forEach(s -> {
                    if (s != null) {
                        if (_config._sources == null)
                            _config._sources = new ArrayList<>();
                        _config._sources.add(s);
                    }
                });
            }

            return (B) this;
        }

        @Override
        public B setTaskExecutor(Consumer<Runnable> taskExecutor) {
            _config._taskExecutor = taskExecutor;
            return (B) this;
        }

        @Override
        public DefaultConfigurationManagerConfig build() {
            if (_config._name == null || _config._name.trim().isEmpty())
                throw new IllegalArgumentException("name is null or empty");
            _config._name = _config._name.trim();

            if (_config._sources == null || _config._sources.isEmpty())
                throw new IllegalArgumentException("sources is null or empty");

            if (_config._taskExecutor == null)
                _config._taskExecutor = DEFAULT_TASK_EXECUTOR;

            return _config.clone();
        }

    }

}
