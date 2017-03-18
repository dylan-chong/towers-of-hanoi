package main.async;

import com.google.inject.Singleton;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Dylan on 17/03/17.
 *
 * Use this for queuing up multiple {@link AsyncTask}
 */
@Singleton
public class AsyncTaskQueues {
    private static final int NUMBER_OF_THREADS =
            Runtime.getRuntime().availableProcessors();

    /**
     * This is required to prevent threads being blocked when adding too many
     * {@link AsyncTask} objects to mainTasksQueue.
     */
    private Queue<AsyncTask> tasksToAdd = new ConcurrentLinkedQueue<>();
    private Thread queuingThread;

    private BlockingQueue<AsyncTask> mainTasksQueue =
            new ArrayBlockingQueue<>(NUMBER_OF_THREADS);
    private Collection<AsyncWorker> workers;

    public AsyncTaskQueues() {
        workers = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_THREADS; i++) {
            workers.add(new AsyncWorker(mainTasksQueue));
        }

        queuingThread = new Thread(this::keepQueuingTasks);
        queuingThread.start();
    }

    public void addTask(AsyncTask task) {
        tasksToAdd.add(task);
    }

    public void keepQueuingTasks() {
        // noinspection InfiniteLoopStatement
        while (true) {
            if (tasksToAdd.isEmpty() || mainTasksQueue.remainingCapacity() == 0) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new AssertionError(e);
                }
                continue;
            }

            mainTasksQueue.add(tasksToAdd.poll());
        }
    }
}
