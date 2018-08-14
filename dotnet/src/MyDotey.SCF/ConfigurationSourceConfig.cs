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
        public abstract string getName();

        public interface Builder : AbstractBuilder<Builder, ConfigurationSourceConfig>
        {

        }

        public interface AbstractBuilder<B, C>
            where B : AbstractBuilder<B, C>
            where C : ConfigurationSourceConfig
        {
            /**
             * required
             * @see ConfigurationSourceConfig#getName()
             */
            B setName(string name);

            C build();
        }
    }
}