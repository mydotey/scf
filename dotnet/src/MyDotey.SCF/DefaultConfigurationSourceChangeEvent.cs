using System;

namespace MyDotey.SCF
{
    /**
     * @author koqizhao
     *
     * Jul 19, 2018
     */
    public class DefaultConfigurationSourceChangeEvent : IConfigurationSourceChangeEvent
    {
        public virtual IConfigurationSource Source { get; private set; }
        public virtual DateTime ChangeTime { get; private set; }

        public DefaultConfigurationSourceChangeEvent(IConfigurationSource source)
            : this(source, DateTime.Now)
        {

        }

        public DefaultConfigurationSourceChangeEvent(IConfigurationSource source, DateTime changeTime)
        {
            if (source == null)
                throw new ArgumentNullException("source is null");

            Source = source;
            ChangeTime = changeTime;
        }

        public override String ToString()
        {
            return string.Format("{0} {{ source: {1}, changeTime: {2} }}", GetType().Name, Source, ChangeTime);
        }
    }
}