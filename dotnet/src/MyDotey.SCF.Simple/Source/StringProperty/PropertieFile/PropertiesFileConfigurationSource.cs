using System;
using System.Collections.Generic;
using System.IO;
using System.Text;

using NLog;
using Kajabity.Tools.Java;

namespace MyDotey.SCF.Source.StringProperty.PropertiesFile
{
    /**
     * @author koqizhao
     *
     * May 17, 2018
     */
    public class PropertiesFileConfigurationSource : StringPropertyConfigurationSource<PropertiesFileConfigurationSourceConfig>
    {
        private static Logger LOGGER = LogManager.GetCurrentClassLogger(typeof(PropertiesFileConfigurationSource));

        private JavaProperties _properties;

        public PropertiesFileConfigurationSource(PropertiesFileConfigurationSourceConfig config)
            : base(config)
        {
            _properties = new JavaProperties();

            try
            {
                using (Stream @is = new FileStream(config.getFileName(), FileMode.Open))
                {
                    if (@is == null)
                        LOGGER.Warn("file not found: {0}", config.getFileName());

                    _properties.Load(@is, new UTF8Encoding(false));
                }
            }
            catch (Exception e)
            {
                LOGGER.Warn(e, "failed to load properties file: " + config.getFileName());
            }
        }

        public override String getPropertyValue(String key)
        {
            if (_properties == null)
                return null;

            return _properties.GetProperty(key);
        }
    }
}