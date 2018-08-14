using System;
using System.Collections.Generic;

using System.Linq;

namespace MyDotey.SCF
{
    /**
     * @author koqizhao
     *
     * May 17, 2018
     */
    public class DefaultConfigurationManagerConfig : ConfigurationManagerConfig, ICloneable
    {
        private string _name;
        private Dictionary<int, ConfigurationSource> _sources;
        private Action<Action> _taskExecutor;

        protected DefaultConfigurationManagerConfig()
        {

        }

        public override string getName()
        {
            return _name;
        }

        public override Dictionary<int, ConfigurationSource> getSources()
        {
            return _sources;
        }

        public override Action<Action> getTaskExecutor()
        {
            return _taskExecutor;
        }

        public virtual object Clone()
        {
            DefaultConfigurationManagerConfig copy = (DefaultConfigurationManagerConfig)MemberwiseClone();
            if (_sources != null)
                copy._sources = new Dictionary<int, ConfigurationSource>(_sources);
            return copy;
        }

        public override string ToString()
        {
            return string.Format("{0} {{ name: {1}, taskExecutor: {2}, sources: [ {3} ] }}", GetType().Name, _name,
                    _taskExecutor, _sources == null ? null : string.Join(", ", _sources));
        }

        public new class Builder : DefaultAbstractBuilder<ConfigurationManagerConfig.Builder, ConfigurationManagerConfig>
                , ConfigurationManagerConfig.Builder
        {

        }

        public abstract class DefaultAbstractBuilder<B, C>
                : ConfigurationManagerConfig.AbstractBuilder<B, C>
                where B : ConfigurationManagerConfig.AbstractBuilder<B, C>
                where C : ConfigurationManagerConfig
        {
            protected static readonly Action<Action> DEFAULT_TASK_EXECUTOR = t => t();

            private DefaultConfigurationManagerConfig _config;

            protected DefaultAbstractBuilder()
            {
                _config = (DefaultConfigurationManagerConfig)(object)newConfig();
            }

            protected virtual C newConfig()
            {
                return (C)(object)(new DefaultConfigurationManagerConfig());
            }

            protected virtual C getConfig()
            {
                return (C)(object)_config;
            }

            public virtual B setName(String name)
            {
                _config._name = name;
                return (B)(object)this;
            }

            public virtual B addSource(int priority, ConfigurationSource source)
            {
                if (source != null)
                {
                    if (_config._sources == null)
                        _config._sources = new Dictionary<int, ConfigurationSource>();
                    else
                    {
                        _config._sources.TryGetValue(priority, out ConfigurationSource existed);
                        if (existed != null)
                            throw new ArgumentException(string.Format(
                                "duplicate source priority, existing: {{ priority: {0}, source: {1} }}, new: {{ priority: {2}, source: {3} }}",
                                priority, existed, priority, source));
                    }
                    _config._sources[priority] = source;
                }

                return (B)(object)this;
            }

            public virtual B addSources(Dictionary<int, ConfigurationSource> sources)
            {
                if (sources != null)
                    sources.ToList().ForEach(p => addSource(p.Key, p.Value));

                return (B)(object)this;
            }

            public virtual B setTaskExecutor(Action<Action> taskExecutor)
            {
                _config._taskExecutor = taskExecutor;
                return (B)(object)this;
            }

            public virtual C build()
            {
                if (string.IsNullOrWhiteSpace(_config._name))
                    throw new ArgumentNullException("name is null or empty");
                _config._name = _config._name.Trim();

                if (_config._sources == null || _config._sources.Count == 0)
                    throw new ArgumentNullException("sources is null or empty");

                if (_config._taskExecutor == null)
                    _config._taskExecutor = DEFAULT_TASK_EXECUTOR;

                return (C)_config.Clone();
            }
        }
    }
}