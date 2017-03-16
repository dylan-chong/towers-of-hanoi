package main.async;

/**
 * Created by Dylan on 17/03/17.
 */
public class AsyncTask {
    public final Runnable doTask;
    public final Runnable onCompletion;

    public AsyncTask(Runnable doTask, Runnable onCompletion) {
        this.doTask = doTask;
        this.onCompletion = onCompletion;
    }
}
