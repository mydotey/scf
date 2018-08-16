using System;

namespace MyDotey.SCF
{
    /**
     * @author koqizhao
     *
     * May 17, 2018
     */
    public abstract class ConfigurationSourceConfig
    {
        /**
         * for description use
         * <p>
         * non-null, non-empty
         */
        public abstract string Name { get; }

        public interface IBuilder : IAbstractBuilder<IBuilder, ConfigurationSourceConfig>
        {

        }

        public interface IAbstractBuilder<B, C>
            where B : IAbstractBuilder<B, C>
            where C : ConfigurationSourceConfig
        {
            /**
             * required
             * @see ConfigurationSourceConfig#getName()
             */
            B SetName(string name);

            C Build();
        }
    }
}