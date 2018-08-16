using System;

namespace MyDotey.SCF
{
    /**
     * @author koqizhao
     *
     * Jul 19, 2018
     */
    public interface ConfigurationSourceChangeEvent
    {
        ConfigurationSource Source { get; }
        long ChangeTime { get; }
    }
}