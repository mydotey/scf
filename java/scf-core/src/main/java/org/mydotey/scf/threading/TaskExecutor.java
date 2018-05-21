package org.mydotey.scf.threading;

import java.io.Closeable;

/**
 * @author koqizhao
 *
 * May 21, 2018
 */
public interface TaskExecutor extends Closeable {

    void schedule(Runnable runnable);

    void submit(Runnable runnable);

    @Override
    void close();

}
