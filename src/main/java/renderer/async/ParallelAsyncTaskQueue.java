package renderer.async;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Dylan on 17/03/17.
 * <p>
 * Use this for queuing up multiple {@link AsyncTask}
 */
@Singleton
public class ParallelAsyncTaskQueue implements AsyncTaskQueue {
    private static final int DEFAULT_NUMBER_OF_THREADS =
            Runtime.getRuntime().availableProcessors();

    /**
     * This is required to prevent threads being blocked when adding too many
     * {@link AsyncTask} objects to mainTasksQueue.
     */
    private Queue<AsyncTask> tasksToAdd = new ConcurrentLinkedQueue<>();
    private Thread queuingThread;

    private BlockingQueue<AsyncTask> mainTasksQueue;
    private Collection<AsyncWorker> workers;

    @Inject
    public ParallelAsyncTaskQueue() {
        this(DEFAULT_NUMBER_OF_THREADS);
    }

    public ParallelAsyncTaskQueue(int numberOfWorkerThreads) {
        mainTasksQueue = new ArrayBlockingQueue<>(numberOfWorkerThreads);

        workers = new ArrayList<>();
        for (int i = 0; i < numberOfWorkerThreads; i++) {
            workers.add(new AsyncWorker(mainTasksQueue));
        }

        queuingThread = new Thread(this::keepQueuingTasks);
        queuingThread.start();
    }

    @Override
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
