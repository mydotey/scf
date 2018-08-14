using System;

namespace MyDotey.SCF.Source.StringProperty.PropertiesFile
{
    /**
     * @author koqizhao
     *
     * May 17, 2018
     */
    public class PropertiesFileConfigurationSourceConfig : DefaultConfigurationSourceConfig
    {
        private String _fileName;

        protected PropertiesFileConfigurationSourceConfig()
        {

        }

        public String getFileName()
        {
            return _fileName;
        }

        public override String ToString()
        {
            return String.Format("{0} {{ name: {1}, fileName: {2} }}", GetType().Name, getName(), getFileName());
        }

        public new class Builder
            : DefaultConfigurationSourceConfig.DefaultAbstractBuilder<Builder, PropertiesFileConfigurationSourceConfig>
        {
            protected override PropertiesFileConfigurationSourceConfig newConfig()
            {
                return new PropertiesFileConfigurationSourceConfig();
            }

            public Builder setFileName(String fileName)
            {
                getConfig()._fileName = fileName;
                return this;
            }

            public override PropertiesFileConfigurationSourceConfig build()
            {
                if (string.IsNullOrWhiteSpace(getConfig().getFileName()))
                    throw new ArgumentNullException("fileName is null or empty");

                getConfig()._fileName = getConfig()._fileName.Trim();
                if (!getConfig()._fileName.EndsWith(".properties"))
                    getConfig()._fileName += ".properties";

                return base.build();
            }
        }
    }
}