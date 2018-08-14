using System;

namespace MyDotey.SCF.Facade
{
    /**
     * @author koqizhao
     *
     * May 21, 2018
     */
    public class StringProperties : StringValueProperties<String>
    {
        public StringProperties(ConfigurationManager manager)
            : base(manager)
        {
        }
    }
}