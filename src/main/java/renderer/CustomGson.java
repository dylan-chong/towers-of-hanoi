package renderer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Configured {@link Gson} object
 */
public class CustomGson {
    private static Gson ourInstance = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    public static Gson getInstance() {
        return ourInstance;
    }

    private CustomGson() {
    }
}
