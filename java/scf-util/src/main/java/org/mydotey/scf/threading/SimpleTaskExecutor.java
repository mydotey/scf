package org.mydotey.scf.threading;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * @author koqizhao
 *
 * May 21, 2018
 */
public class SimpleTaskExecutor implements TaskExecutor {

    private ScheduledExecutorService _scheduledExecutorService;

    public SimpleTaskExecutor(int corePoolSize, ThreadFactory threadFactory) {
        _scheduledExecutorService = Executors.newScheduledThreadPool(corePoolSize, threadFactory);
    }

    @Override
    public void schedule(Runnable runnable, long delayMs, long intervalMs) {
        _scheduledExecutorService.scheduleWithFixedDelay(runnable, delayMs, intervalMs, TimeUnit.MILLISECONDS);
    }

    @Override
    public void submit(Runnable runnable) {
        _scheduledExecutorService.submit(runnable);
    }

    @Override
    public void close() {
        _scheduledExecutorService.shutdown();
    }

}
