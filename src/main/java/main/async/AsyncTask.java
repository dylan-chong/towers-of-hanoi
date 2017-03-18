package main.async;

/**
 * Created by Dylan on 17/03/17.
 */
public class AsyncTask {
    public final Runnable doTask;
    public final Runnable onCompletion;
    public final String name;

    public AsyncTask(Runnable doTask,
                     Runnable onCompletion,
                     String name) {
        this.doTask = doTask;
        this.onCompletion = onCompletion;
        this.name = name;
    }
}
