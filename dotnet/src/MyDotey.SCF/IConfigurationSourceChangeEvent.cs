using System;

namespace MyDotey.SCF
{
    /**
     * @author koqizhao
     *
     * Jul 19, 2018
     */
    public interface IConfigurationSourceChangeEvent
    {
        IConfigurationSource Source { get; }
        long ChangeTime { get; }
    }
}