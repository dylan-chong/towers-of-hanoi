package main.async;

/**
 * Created by Dylan on 17/03/17.
 */
public class AsyncTask {
    public final Runnable doTask;
    public final String name;

    public AsyncTask(Runnable doTask,
                     String name) {
        this.doTask = doTask;
        this.name = name;
    }
}
