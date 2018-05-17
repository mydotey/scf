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

    /*
     * case
     *  0: not use a thread pool, handle changes in the source change raising thread
     *  > 0: handle changes in a standalone thread pool
     * default to 1
     */
    int getChangeHandlerThreadPoolSize();

    public interface Builder extends AbstractBuilder<Builder> {

    }

    public interface AbstractBuilder<B extends AbstractBuilder<B>> {

        B setName(String name);

        B setSources(Collection<ConfigurationSource> sources);

        B setChangeHandlerThreadPoolSize(int changeHandlerThreadPoolSize);

        ConfigurationManagerConfig build();

    }

}
