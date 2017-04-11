package renderer;

import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import renderer.async.AsyncTaskQueue;
import renderer.async.ParallelAsyncTaskQueue;

/**
 * Created by Dylan on 14/03/17.
 */
public class Main {
    public static void main(String[] args) {
        Injector mainInjector = Guice.createInjector(new MainModule());
        MainController controller = mainInjector.getInstance(MainController.class);
    }

    private static class MainModule implements Module {
        @Override
        public void configure(Binder binder) {
            binder.bind(AsyncTaskQueue.class).to(ParallelAsyncTaskQueue.class);
        }
    }
}
