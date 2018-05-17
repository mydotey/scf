package org.mydotey.scf;

import java.util.Collection;

/**
 * @author koqizhao
 *
 * May 17, 2018
 */
public interface ConfigurationManagerConfig {

    String getName();

    Collection<ConfigurationSource> getSources();

    public interface Builder extends AbstractBuilder<Builder> {

    }

    public interface AbstractBuilder<B extends AbstractBuilder<B>> {

        B setName(String name);

        B setSources(Collection<ConfigurationSource> sources);

        ConfigurationManagerConfig build();
    }

}
