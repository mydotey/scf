package org.mydotey.scf;

import java.io.Closeable;

/**
 * @author koqizhao
 *
 * May 21, 2018
 */
public interface TaskExecutor extends Closeable {

    void schedule(Runnable runnable, long delayMs, long intervalMs);

    void submit(Runnable runnable);

    @Override
    void close();

}
