using System;

namespace MyDotey.SCF
{
    public interface PropertyChangeEvent
    {
        Property getProperty();

        object getOldValue();

        object getNewValue();

        long getChangeTime();
    }

    /**
     * @author koqizhao
     *
     * Jul 19, 2018
     */
    public interface PropertyChangeEvent<K, V> : PropertyChangeEvent
    {
        new Property<K, V> getProperty();

        new V getOldValue();

        new V getNewValue();
    }
}