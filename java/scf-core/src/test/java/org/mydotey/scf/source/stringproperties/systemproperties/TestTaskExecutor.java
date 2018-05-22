package org.mydotey.scf.source.stringproperties.systemproperties;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.mydotey.scf.threading.TaskExecutor;

/**
 * @author koqizhao
 *
 * May 21, 2018
 */
public class TestTaskExecutor implements TaskExecutor {

    private ScheduledExecutorService _scheduledExecutorService;
    private long _delayMs;
    private long _intervalMs;

    public TestTaskExecutor(long delayMs, long intervalMs) {
        _scheduledExecutorService = Executors.newScheduledThreadPool(1);
        _delayMs = delayMs;
        _intervalMs = intervalMs;
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
