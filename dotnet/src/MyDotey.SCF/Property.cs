using System;

namespace MyDotey.SCF
{
    public interface Property
    {
        PropertyConfig getConfig();
        object getValue();
    }

    /**
     * @author koqizhao
     *
     * May 16, 2018
     */
    public interface Property<K, V> : Property
    {
        /**
         * @see PropertyConfig
         */
        new PropertyConfig<K, V> getConfig();

        /**
         * property value, if not configured or no valid value, default to defaultValue Of PropertyConfig
         * <p>
         * @see PropertyConfig#getDefaultValue()
         */
        new V getValue();

        /**
         * listeners to the value change, notified once value changed
         */
        void addChangeListener(Action<PropertyChangeEvent<K, V>> changeListener);
    }
}