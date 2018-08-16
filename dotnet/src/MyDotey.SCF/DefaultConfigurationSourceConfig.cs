using System;

namespace MyDotey.SCF
{
    /**
     * @author koqizhao
     *
     * May 17, 2018
     */
    public class DefaultConfigurationSourceConfig : ConfigurationSourceConfig, ICloneable
    {
        private string _name;

        protected DefaultConfigurationSourceConfig()
        {

        }

        public override string Name { get { return _name; } }

        public virtual object Clone()
        {
            return MemberwiseClone();
        }

        public override string ToString()
        {
            return string.Format("{0} {{ name: {1} }}", GetType().Name, _name);
        }

        public class Builder : DefaultAbstractBuilder<ConfigurationSourceConfig.IBuilder, ConfigurationSourceConfig>
                , ConfigurationSourceConfig.IBuilder
        {

        }

        public abstract class DefaultAbstractBuilder<B, C>
                : ConfigurationSourceConfig.IAbstractBuilder<B, C>
                where B : ConfigurationSourceConfig.IAbstractBuilder<B, C>
                where C : ConfigurationSourceConfig
        {
            private DefaultConfigurationSourceConfig _config;

            protected DefaultAbstractBuilder()
            {
                _config = (DefaultConfigurationSourceConfig)(object)NewConfig();
            }

            protected virtual C NewConfig()
            {
                return (C)(object)(new DefaultConfigurationSourceConfig());
            }

            protected virtual C Config { get { return (C)(object)_config; } }

            public virtual B SetName(String name)
            {
                _config._name = name;
                return (B)(object)this;
            }

            public virtual C Build()
            {
                if (string.IsNullOrWhiteSpace(_config._name))
                    throw new ArgumentNullException("name is null or empty");

                _config._name = _config._name.Trim();

                return (C)_config.Clone();
            }
        }
    }
}