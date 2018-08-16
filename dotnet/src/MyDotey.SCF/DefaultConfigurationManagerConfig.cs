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
        private Dictionary<int, IConfigurationSource> _sources;
        private Action<Action> _taskExecutor;

        protected DefaultConfigurationManagerConfig()
        {

        }

        public override string Name { get { return _name; } }

        public override Dictionary<int, IConfigurationSource> Sources { get { return _sources; } }

        public override Action<Action> TaskExecutor { get { return _taskExecutor; } }

        public virtual object Clone()
        {
            DefaultConfigurationManagerConfig copy = (DefaultConfigurationManagerConfig)MemberwiseClone();
            if (_sources != null)
                copy._sources = new Dictionary<int, IConfigurationSource>(_sources);
            return copy;
        }

        public override string ToString()
        {
            return string.Format("{0} {{ name: {1}, taskExecutor: {2}, sources: [ {3} ] }}", GetType().Name, _name,
                    _taskExecutor, _sources == null ? null : string.Join(", ", _sources));
        }

        public class Builder : DefaultAbstractBuilder<ConfigurationManagerConfig.IBuilder, ConfigurationManagerConfig>
                , ConfigurationManagerConfig.IBuilder
        {

        }

        public abstract class DefaultAbstractBuilder<B, C>
                : ConfigurationManagerConfig.IAbstractBuilder<B, C>
                where B : ConfigurationManagerConfig.IAbstractBuilder<B, C>
                where C : ConfigurationManagerConfig
        {
            protected static readonly Action<Action> DEFAULT_TASK_EXECUTOR = t => t();

            private DefaultConfigurationManagerConfig _config;

            protected DefaultAbstractBuilder()
            {
                _config = (DefaultConfigurationManagerConfig)(object)NewConfig();
            }

            protected virtual C NewConfig()
            {
                return (C)(object)(new DefaultConfigurationManagerConfig());
            }

            protected virtual C Config { get { return (C)(object)_config; } }

            public virtual B SetName(String name)
            {
                _config._name = name;
                return (B)(object)this;
            }

            public virtual B AddSource(int priority, IConfigurationSource source)
            {
                if (source != null)
                {
                    if (_config._sources == null)
                        _config._sources = new Dictionary<int, IConfigurationSource>();
                    else
                    {
                        _config._sources.TryGetValue(priority, out IConfigurationSource existed);
                        if (existed != null)
                            throw new ArgumentException(string.Format(
                                "duplicate source priority, existing: {{ priority: {0}, source: {1} }}, new: {{ priority: {2}, source: {3} }}",
                                priority, existed, priority, source));
                    }
                    _config._sources[priority] = source;
                }

                return (B)(object)this;
            }

            public virtual B AddSources(Dictionary<int, IConfigurationSource> sources)
            {
                if (sources != null)
                    sources.ToList().ForEach(p => AddSource(p.Key, p.Value));

                return (B)(object)this;
            }

            public virtual B SetTaskExecutor(Action<Action> taskExecutor)
            {
                _config._taskExecutor = taskExecutor;
                return (B)(object)this;
            }

            public virtual C Build()
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