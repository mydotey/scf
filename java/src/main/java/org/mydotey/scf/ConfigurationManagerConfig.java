package org.mydotey.scf;

import java.util.Map;
import java.util.function.Consumer;

/**
 * @author koqizhao
 *
 * May 17, 2018
 */
public interface ConfigurationManagerConfig {

    /**
     * for description use
     * <p>
     * non-null, non-empty
     */
    String getName();

    /**
     * key for the source priority, value for the source
     * <p>
     * the greater the key is, the higher the priority is
     * <p>
     * non-null, non-empty
     */
    Map<Integer, ConfigurationSource> getSources();

    /**
     * thread pool for property value update and property change listeners
     * <p>
     * by default, property value update and property change listeners
     * will be done in the source change raising thread
     * <p>
     * if property count is too large, or the property change listeners are too slow,
     * it's better to use an async thread pool
     * <p>
     * a simple Thread Pool utility: @see org.mydotey.scf.threading.TaskExecutor
     */
    Consumer<Runnable> getTaskExecutor();

    public interface Builder extends AbstractBuilder<Builder, ConfigurationManagerConfig> {

    }

    public interface AbstractBuilder<B extends AbstractBuilder<B, C>, C extends ConfigurationManagerConfig> {

        /**
         * required
         * @see ConfigurationManagerConfig#getName()
         */
        B setName(String name);

        /**
         * required
         * @see ConfigurationManagerConfig#getSources()
         */
        B addSource(int priority, ConfigurationSource source);

        /**
         * required
         * @see ConfigurationManagerConfig#getSources()
         */
        B addSources(Map<Integer, ConfigurationSource> sources);

        /**
         * optional
         * @see ConfigurationManagerConfig#getTaskExecutor()
         */
        B setTaskExecutor(Consumer<Runnable> taskExecutor);

        C build();

    }

}
