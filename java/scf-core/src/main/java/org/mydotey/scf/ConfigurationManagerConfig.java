package org.mydotey.scf;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * @author koqizhao
 *
 * May 17, 2018
 */
public interface ConfigurationManagerConfig {

    String getName();

    Collection<ConfigurationSource> getSources();

    Consumer<Runnable> getTaskExecutor();

    public interface Builder extends AbstractBuilder<Builder> {

    }

    public interface AbstractBuilder<B extends AbstractBuilder<B>> {

        B setName(String name);

        B addSource(ConfigurationSource source);

        B addSources(Collection<ConfigurationSource> sources);

        B setTaskExecutor(Consumer<Runnable> taskExecutor);

        ConfigurationManagerConfig build();

    }

}
