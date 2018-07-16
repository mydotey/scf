package org.mydotey.scf;

/**
 * @author koqizhao
 *
 * May 17, 2018
 */
public interface ConfigurationSourceConfig {

    String getName();

    public interface Builder extends AbstractBuilder<Builder> {

    }

    public interface AbstractBuilder<B extends AbstractBuilder<B>> {

        B setName(String name);

        ConfigurationSourceConfig build();
    }

}
