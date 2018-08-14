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

        public override string getName()
        {
            return _name;
        }

        public virtual object Clone()
        {
            return MemberwiseClone();
        }

        public override string ToString()
        {
            return string.Format("{0} {{ name: {1} }}", GetType().Name, _name);
        }

        public new class Builder : DefaultAbstractBuilder<ConfigurationSourceConfig.Builder, ConfigurationSourceConfig>
                , ConfigurationSourceConfig.Builder
        {

        }

        public abstract class DefaultAbstractBuilder<B, C>
                : ConfigurationSourceConfig.AbstractBuilder<B, C>
                where B : ConfigurationSourceConfig.AbstractBuilder<B, C>
                where C : ConfigurationSourceConfig
        {
            private DefaultConfigurationSourceConfig _config;

            protected DefaultAbstractBuilder()
            {
                _config = (DefaultConfigurationSourceConfig)(object)newConfig();
            }

            protected virtual C newConfig()
            {
                return (C)(object)(new DefaultConfigurationSourceConfig());
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

            public virtual C build()
            {
                if (string.IsNullOrWhiteSpace(_config._name))
                    throw new ArgumentNullException("name is null or empty");

                _config._name = _config._name.Trim();

                return (C)_config.Clone();
            }
        }
    }
}