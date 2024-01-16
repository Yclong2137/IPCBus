package com.ycl.ipc;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 线程池
 */
public final class HiExecutor {

    private final ThreadPoolExecutor executor;

    public static HiExecutor getInstance() {
        return HiExecutor.Holder.INSTANCE;
    }

    private HiExecutor() {
        int cpuCount = Runtime.getRuntime().availableProcessors();
        int corePoolSize = cpuCount + 1;
        int maxPoolSize = cpuCount * 2 + 1;
        BlockingQueue<Runnable> blockingQueue = new PriorityBlockingQueue<>();
        long keepAliveTime = 30L;
        TimeUnit unit = TimeUnit.SECONDS;
        final AtomicLong seq = new AtomicLong();
        ThreadFactory threadFactory = new ThreadFactory() {
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("HiExecutor-" + seq.getAndIncrement());
                return thread;
            }
        };
        this.executor = new ThreadPoolExecutor(corePoolSize, maxPoolSize, keepAliveTime, unit, blockingQueue, threadFactory);
    }

    public void execute(int priority, Runnable r) {
        this.executor.execute(new HiExecutor.PriorityRunnable(priority, r));
    }

    public void execute(Runnable r) {
        this.execute(5, r);
    }


    public void close() {
        this.executor.shutdown();
    }

    private static class PriorityRunnable implements Runnable, Comparable<HiExecutor.PriorityRunnable> {
        private final int priority;
        private final Runnable r;

        public PriorityRunnable(int priority, Runnable r) {
            this.priority = priority;
            this.r = r;
        }

        public void run() {
            if (this.r != null) {
                this.r.run();
            }

        }

        public int compareTo(HiExecutor.PriorityRunnable other) {
            if (this.priority < other.priority) {
                return 1;
            } else {
                return this.priority > other.priority ? -1 : 0;
            }
        }
    }

    private static final class Holder {
        private static final HiExecutor INSTANCE = new HiExecutor();

        private Holder() {
        }
    }
}
