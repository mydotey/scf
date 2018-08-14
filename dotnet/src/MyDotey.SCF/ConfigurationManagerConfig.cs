using System;
using System.Collections.Generic;

namespace MyDotey.SCF
{
    /**
     * @author koqizhao
     *
     * May 17, 2018
     */
    public abstract class ConfigurationManagerConfig
    {
        /**
         * for description use
         * <p>
         * non-null, non-empty
         */
        public abstract String getName();

        /**
         * key for the source priority, value for the source
         * <p>
         * the greater the key is, the higher the priority is
         * <p>
         * non-null, non-empty
         */
        public abstract Dictionary<int, ConfigurationSource> getSources();

        /**
         * thread pool for property value update and property change listeners
         * <p>
         * by default, property value update and property change listeners
         * will be done in the source change raising thread
         * <p>
         * if property count is too large, or the property change listeners are too slow,
         * it's better to use an async thread pool
         * <p>
         * a simple Thread Pool utility: @see org.mydotey.scf.threading.TaskExecutor
         */
        public abstract Action<Action> getTaskExecutor();

        public interface Builder : AbstractBuilder<Builder, ConfigurationManagerConfig>
        {

        }

        public interface AbstractBuilder<B, C>
            where B : AbstractBuilder<B, C>
            where C : ConfigurationManagerConfig
        {
            /**
             * required
             * @see ConfigurationManagerConfig#getName()
             */
            B setName(String name);

            /**
             * required
             * @see ConfigurationManagerConfig#getSources()
             */
            B addSource(int priority, ConfigurationSource source);

            /**
             * required
             * @see ConfigurationManagerConfig#getSources()
             */
            B addSources(Dictionary<int, ConfigurationSource> sources);

            /**
             * optional
             * @see ConfigurationManagerConfig#getTaskExecutor()
             */
            B setTaskExecutor(Action<Action> taskExecutor);

            C build();
        }
    }
}