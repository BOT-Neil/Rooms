package tel.endho.rooms.Tasks;

import com.grinderwolf.swm.api.exceptions.WorldAlreadyExistsException;
import com.plotsquared.core.PlotAPI;
import com.plotsquared.core.plot.Plot;
import tel.endho.rooms.Rooms;

import java.io.IOException;
import java.sql.SQLException;
//import java.time.Instant;
import java.util.concurrent.atomic.AtomicBoolean;

public class MigrateOnePlot implements Runnable{
    @Override
    public void run() {
        PlotAPI api = new PlotAPI();
        AtomicBoolean migratedOne= new AtomicBoolean(false);
        for(Plot plot :api.getAllPlots()) {
            if (!migratedOne.get() && !plot.isMerged()) {
                try {
                    //api.getPlotSquared().getImpromptuUUIDPipeline().get
                    //Instant instant = Instant.ofEpochSecond(plot.getTimestamp());
                    plot.isMerged();
                    Rooms.roomWorldManager.migratePlot(plot);
                    migratedOne.set(true);
                    break;
                } catch (SQLException | IOException | WorldAlreadyExistsException e) {
                    e.printStackTrace();
                }
            }

        }

    }
}
