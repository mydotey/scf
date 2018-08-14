using System;
using System.Collections.Generic;

namespace MyDotey.SCF.Source.StringProperty.Cascaded
{
    /**
     * @author koqizhao
     *
     * May 17, 2018
     */
    public class CascadedConfigurationSourceConfig<C> : DefaultConfigurationSourceConfig
        where C : ConfigurationSourceConfig
    {
        private string _keySeparator;
        private List<string> _cascadedFactors;
        private StringPropertyConfigurationSource<C> _source;

        protected CascadedConfigurationSourceConfig()
        {

        }

        public string getKeySeparator()
        {
            return _keySeparator;
        }

        public List<string> getCascadedFactors()
        {
            return _cascadedFactors;
        }

        public StringPropertyConfigurationSource<C> getSource()
        {
            return _source;
        }

        public override object Clone()
        {
            CascadedConfigurationSourceConfig<C> copy = (CascadedConfigurationSourceConfig<C>)base.Clone();
            if (_cascadedFactors != null)
                copy._cascadedFactors = new List<string>(_cascadedFactors);
            return copy;
        }

        public override string ToString()
        {
            return string.Format("{0} {{ name: {1}, keySeparator: {2}, cascadedFactors: [ {3} ], source {4} }}", GetType().Name,
                getName(), _keySeparator, _cascadedFactors == null ? null : string.Join(", ", _cascadedFactors), _source);
        }

        public new class Builder : DefaultConfigurationSourceConfig.DefaultAbstractBuilder<Builder, CascadedConfigurationSourceConfig<C>>
        {
            protected override CascadedConfigurationSourceConfig<C> newConfig()
            {
                return new CascadedConfigurationSourceConfig<C>();
            }

            public virtual Builder setKeySeparator(string keySeparator)
            {
                getConfig()._keySeparator = keySeparator;
                return this;
            }

            public virtual Builder addCascadedFactor(string cascadedFactor)
            {
                if (string.IsNullOrWhiteSpace(cascadedFactor))
                    return this;

                cascadedFactor = cascadedFactor.Trim();
                if (getConfig()._cascadedFactors == null)
                    getConfig()._cascadedFactors = new List<string>();
                getConfig()._cascadedFactors.Add(cascadedFactor);

                return this;
            }

            public virtual Builder addCascadedFactors(List<string> cascadedFactors)
            {
                if (cascadedFactors != null)
                    cascadedFactors.ForEach(f => addCascadedFactor(f));

                return this;
            }

            public virtual Builder setSource(StringPropertyConfigurationSource<C> source)
            {
                getConfig()._source = source;

                return this;
            }

            public override CascadedConfigurationSourceConfig<C> build()
            {
                if (string.IsNullOrWhiteSpace(getConfig()._keySeparator))
                    throw new ArgumentNullException("keySeparator is null or whitespace");
                getConfig()._keySeparator = getConfig().getKeySeparator().Trim();

                if (getConfig()._cascadedFactors == null || getConfig()._cascadedFactors.Count == 0)
                    throw new ArgumentNullException("cascadedFactors is null or empty");

                if (getConfig()._source == null)
                    throw new ArgumentNullException("source is null");

                return base.build();
            }
        }
    }
}