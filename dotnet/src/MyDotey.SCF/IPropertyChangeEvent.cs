using System;

namespace MyDotey.SCF
{
    public interface IPropertyChangeEvent
    {
        IProperty Property { get; }

        object OldValue { get; }

        object NewValue { get; }

        long ChangeTime { get; }
    }

    /**
     * @author koqizhao
     *
     * Jul 19, 2018
     */
    public interface IPropertyChangeEvent<K, V> : IPropertyChangeEvent
    {
        new IProperty<K, V> Property { get; }

        new V OldValue { get; }

        new V NewValue { get; }
    }
}