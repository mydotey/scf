package org.mydotey.scf;

import java.io.Closeable;
import java.util.Collection;

/**
 * @author koqizhao
 *
 * May 17, 2018
 */
public interface ConfigurationManagerConfig {

    String getName();

    Collection<ConfigurationSource> getSources();

    TaskExecutor getTaskExecutor();

    public interface Builder extends AbstractBuilder<Builder> {

    }

    public interface AbstractBuilder<B extends AbstractBuilder<B>> {

        B setName(String name);

        B setSources(Collection<ConfigurationSource> sources);

        B setTaskExecutor(TaskExecutor taskExecutor);

        ConfigurationManagerConfig build();

    }

    public interface TaskExecutor extends Closeable {

        void schedule(Runnable runnable, long delayMs, long intervalMs);

        void submit(Runnable runnable);

        @Override
        void close();

    }
}
