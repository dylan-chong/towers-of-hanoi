package main.mapdata.model;

import com.google.inject.Inject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Collections;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by Dylan on 29/03/17.
 *
 * Facade for creating a new {@link MapDataModel} from input files
 */
public class MapDataLoader {

    private final MapDataParser dataParser;
    private final MapDataModel.Factory mapModelFactory;
    private final MapDataContainer.Factory dataContainerFactory;

    @Inject
    public MapDataLoader(MapDataParser dataParser,
                         MapDataModel.Factory modelFactory,
                         MapDataContainer.Factory dataContainerFactory) {
        this.dataParser = dataParser;
        this.mapModelFactory = modelFactory;
        this.dataContainerFactory = dataContainerFactory;
    }

    public void load(File nodes,
                     File roads,
                     File segments,
                     File polygons,
                     OnFinishLoad onFinishLoad) {
        try {
            long loadStartTime = System.currentTimeMillis();

            BufferedReader nodesReader = new BufferedReader(new FileReader(nodes));
            BufferedReader segmentsReader = new BufferedReader(new FileReader(segments));
            Scanner roadInfoScanner = new Scanner(roads);
            BufferedReader polygonsReader = (polygons == null) ? null :
                    new BufferedReader(new FileReader(polygons));


            AtomicReference<MapDataModel> mapDataRef = new AtomicReference<>();

            MapDataContainer mapDataContainer = dataContainerFactory.create(
                    () -> onFinishLoad.onLoad(
                            mapDataRef.get(),
                            System.currentTimeMillis() - loadStartTime
                    ),
                    () -> dataParser.parseNodes(nodesReader),
                    () -> dataParser.parseRoadSegments(segmentsReader),
                    () -> dataParser.parseRoadInfo(roadInfoScanner),
                    () -> {
                        if (polygonsReader != null) {
                            return dataParser.parsePolygons(polygonsReader);
                        } else {
                            return Collections.emptyList();
                        }
                    }
            );
            mapDataRef.set(mapModelFactory.create(mapDataContainer));
        } catch (FileNotFoundException e) {
            throw new AssertionError(e);
        }
    }

    public interface OnFinishLoad {
        void onLoad(MapDataModel newModel, long loadDuration);
    }
}
