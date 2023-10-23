package com.github.tezvn.lunix.queue;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ThreadWorker {

    private final ExecutorService threads;

    public ThreadWorker() {
        this(5);
    }

    public ThreadWorker(int size) {
        this.threads = Executors.newFixedThreadPool(size);
    }

    public Future<?> submit(Runnable runnable) {
        return this.threads.submit(runnable);
    }

    public Future<?> submit(Callable<?> callable) {
        return this.threads.submit(callable);
    }

    public Future<?> submit(Runnable runnable, Object object) {
        return this.threads.submit(runnable, object);
    }

}
