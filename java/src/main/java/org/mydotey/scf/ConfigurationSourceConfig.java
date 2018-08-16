package org.mydotey.scf;

/**
 * @author koqizhao
 *
 * May 17, 2018
 */
public interface ConfigurationSourceConfig {

    /**
     * for description use
     * <p>
     * non-null, non-empty
     */
    String getName();

    public interface Builder extends AbstractBuilder<Builder, ConfigurationSourceConfig> {

    }

    public interface AbstractBuilder<B extends AbstractBuilder<B, C>, C extends ConfigurationSourceConfig> {

        /**
         * required
         * @see ConfigurationSourceConfig#getName()
         */
        B setName(String name);

        C build();
    }

}
