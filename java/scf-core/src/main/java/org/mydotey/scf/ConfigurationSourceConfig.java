package org.mydotey.scf;

/**
 * @author koqizhao
 *
 * May 17, 2018
 */
public interface ConfigurationSourceConfig {

    String getName();

    int getPriority();

    public interface Builder extends AbstractBuilder<Builder> {

    }

    public interface AbstractBuilder<B extends AbstractBuilder<B>> {

        B setName(String name);

        B setPriority(int priority);

        ConfigurationSourceConfig build();
    }

}
