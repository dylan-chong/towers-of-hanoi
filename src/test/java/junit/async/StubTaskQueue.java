package junit.async;

import main.async.AsyncTask;
import main.async.AsyncTaskQueue;

/**
 * Created by Dylan on 30/03/17.
 *
 * Turns {@link main.async.AsyncTaskQueue} into a synchronous one for testing
 */
public class StubTaskQueue implements AsyncTaskQueue {
    @Override
    public void addTask(AsyncTask task) {
        task.doTask.run();
    }
}
