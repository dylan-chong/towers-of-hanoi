package main.async;

/**
 * Created by Dylan on 17/03/17.
 */
public class AsyncTask {
    public final Runnable doTask;
    public final String name;

    public AsyncTask(String name,
                     Runnable doTask) {
        this.doTask = doTask;
        this.name = name;
    }
}
