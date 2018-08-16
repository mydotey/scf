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
        public abstract String Name { get; }

        /**
         * key for the source priority, value for the source
         * <p>
         * the greater the key is, the higher the priority is
         * <p>
         * non-null, non-empty
         */
        public abstract Dictionary<int, IConfigurationSource> Sources { get; }

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
        public abstract Action<Action> TaskExecutor { get; }

        public interface IBuilder : IAbstractBuilder<IBuilder, ConfigurationManagerConfig>
        {

        }

        public interface IAbstractBuilder<B, C>
            where B : IAbstractBuilder<B, C>
            where C : ConfigurationManagerConfig
        {
            /**
             * required
             * @see ConfigurationManagerConfig#getName()
             */
            B SetName(String name);

            /**
             * required
             * @see ConfigurationManagerConfig#getSources()
             */
            B AddSource(int priority, IConfigurationSource source);

            /**
             * required
             * @see ConfigurationManagerConfig#getSources()
             */
            B AddSources(Dictionary<int, IConfigurationSource> sources);

            /**
             * optional
             * @see ConfigurationManagerConfig#getTaskExecutor()
             */
            B SetTaskExecutor(Action<Action> taskExecutor);

            C Build();
        }
    }
}