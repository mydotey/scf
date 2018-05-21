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
    private long _delayMs;
    private long _intervalMs;

    public SimpleTaskExecutor(int corePoolSize, ThreadFactory threadFactory) {
        _scheduledExecutorService = Executors.newScheduledThreadPool(corePoolSize, threadFactory);
        _delayMs = getDelayMs();
        _intervalMs = getIntervalMs();
    }

    protected long getDelayMs() {
        return TimeUnit.SECONDS.toMillis(60);
    }

    protected long getIntervalMs() {
        return TimeUnit.SECONDS.toMillis(60);
    }

    @Override
    public void schedule(Runnable runnable) {
        _scheduledExecutorService.scheduleWithFixedDelay(runnable, _delayMs, _intervalMs, TimeUnit.MILLISECONDS);
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
