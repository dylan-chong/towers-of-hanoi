package main.async;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Dylan on 17/03/17.
 *
 * Does work on its own {@link Thread}.
 */
public class AsyncWorker {
    private final BlockingQueue<AsyncTask> queue;

    public AsyncWorker(BlockingQueue<AsyncTask> queue) {
        this.queue = queue;

        Thread thread = new Thread(this::workOrSleepLoop);
        thread.start();
    }

    /**
     * Waits until a task is available, then does work, then repeats
     */
    private void workOrSleepLoop() {
        // noinspection InfiniteLoopStatement
        while (true) {
            try {
                AsyncTask task = queue.take();
                task.doTask.run();
                task.onCompletion.run();
                System.out.println("Task completed");
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new AssertionError(e);
            }
        }
    }
}
