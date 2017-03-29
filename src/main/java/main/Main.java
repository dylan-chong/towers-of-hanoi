package main;

import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import main.gui.helpers.Drawer;
import main.gui.MapController;
import main.gui.helpers.MapMouseListener;
import main.mapdata.model.MapDataContainer;
import main.mapdata.model.MapDataModel;

/**
 * Created by Dylan on 14/03/17.
 */
public class Main {
    public static void main(String[] args) {
        Injector mainInjector = Guice.createInjector(new MainModule());
        MapController controller = mainInjector.getInstance(MapController.class);
    }

    private static class MainModule implements Module {
        @Override
        public void configure(Binder binder) {
            binder.install(new FactoryModuleBuilder()
                    .implement(MapDataModel.class, MapDataModel.class)
                    .build(MapDataModel.Factory.class));

            binder.install(new FactoryModuleBuilder()
                    .implement(MapDataContainer.class, MapDataContainer.class)
                    .build(MapDataContainer.Factory.class));

            binder.install(new FactoryModuleBuilder()
                    .implement(Drawer.class, Drawer.class)
                    .build(Drawer.Factory.class));

            binder.install(new FactoryModuleBuilder()
                    .implement(MapMouseListener.class, MapMouseListener.class)
                    .build(MapMouseListener.Factory.class));
        }
    }
}
