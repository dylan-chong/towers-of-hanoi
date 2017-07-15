package main.event;

import com.google.inject.Singleton;

/**
 * Created by Dylan on 13/07/17.
 */
public class Events {
    @Singleton
    public static class AppReady extends Event<Void> {
    }

    @Singleton
    public static class OutputText extends Event<String> {
    }
}
