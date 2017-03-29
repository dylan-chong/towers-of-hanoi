package main;

import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import main.mapdata.model.MapDataContainer;
import main.mapdata.model.MapDataModel;
import slightlymodifiedtemplate.GUI;

/**
 * Created by Dylan on 14/03/17.
 */
public class Main {
    public static void main(String[] args) {
        Injector mainInjector = Guice.createInjector(new MainModule());
        GUI gui = mainInjector.getInstance(GUI.class);
    }

    private static class MainModule implements Module {
        @Override
        public void configure(Binder binder) {
            binder.bind(GUI.class).to(MapGUI.class);

            binder.install(new FactoryModuleBuilder()
                    .implement(MapDataModel.class, MapDataModel.class)
                    .build(MapDataModel.Factory.class));

            binder.install(new FactoryModuleBuilder()
                    .implement(MapDataContainer.class, MapDataContainer.class)
                    .build(MapDataContainer.Factory.class));

            binder.install(new FactoryModuleBuilder()
                    .implement(Drawer.class, Drawer.class)
                    .build(Drawer.Factory.class));
        }
    }
}
