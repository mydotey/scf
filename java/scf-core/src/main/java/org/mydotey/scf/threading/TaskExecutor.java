package org.mydotey.scf.threading;

import java.io.Closeable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.function.Consumer;

/**
 * @author koqizhao
 *
 * May 21, 2018
 * 
 * Simple Thread Pool
 */
public class TaskExecutor implements Consumer<Runnable>, Closeable {

    protected static final ThreadFactory DEFAULT_THREAD_FACTORY = new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread();
            thread.setDaemon(true);
            thread.setName("configuration-manager-task-executor");
            return thread;
        }
    };

    private ExecutorService _executorService;

    public TaskExecutor(int nThreads) {
        this(nThreads, DEFAULT_THREAD_FACTORY);
    }

    public TaskExecutor(int nThreads, ThreadFactory threadFactory) {
        _executorService = Executors.newFixedThreadPool(nThreads, threadFactory);
    }

    @Override
    public void accept(Runnable t) {
        _executorService.submit(t);
    }

    @Override
    public void close() {
        _executorService.shutdown();
    }

}
