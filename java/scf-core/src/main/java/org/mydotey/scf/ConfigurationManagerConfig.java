package org.mydotey.scf;

import java.util.Map;
import java.util.function.Consumer;

/**
 * @author koqizhao
 *
 * May 17, 2018
 */
public interface ConfigurationManagerConfig {

    String getName();

    Map<Integer, ConfigurationSource> getSources();

    Consumer<Runnable> getTaskExecutor();

    public interface Builder extends AbstractBuilder<Builder> {

    }

    public interface AbstractBuilder<B extends AbstractBuilder<B>> {

        B setName(String name);

        B addSource(int priority, ConfigurationSource source);

        B addSources(Map<Integer, ConfigurationSource> sources);

        B setTaskExecutor(Consumer<Runnable> taskExecutor);

        ConfigurationManagerConfig build();

    }

}
