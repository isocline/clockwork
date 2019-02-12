package isocline.clockwork.examples;

import isocline.clockwork.*;
import org.apache.log4j.Logger;

import java.net.MalformedURLException;


/**
 * This program is an indicator program which checks the water level periodically.
 * The water level grows up then Program check more often,
 * but it was a level down then check sometimes.
 *
 */
public class WaterLevelIndicator implements Work {

    private static Logger logger = Logger.getLogger(WaterLevelIndicator.class.getName());


    private int DANGER = 150;

    private int WARN = 120;

    private int NORMAL = 100;

    private int waterLevel = NORMAL;


    public WaterLevelIndicator() throws MalformedURLException {

    }

    private int checkWaterLevel() {

        int x = 400000 / (40000 - waterLevel * waterLevel);


        int gap = (int) (Math.random() * 100 - x) / 5;

        waterLevel = waterLevel + gap;

        return waterLevel;

    }


    public long execute(EventInfo event) throws InterruptedException {

        int waterLevel = checkWaterLevel();

        logger.info("LEVEL : " + waterLevel);

        if (waterLevel >= DANGER) {
            return 1 * Clock.SECOND;

        } else if (waterLevel >= WARN) {
            return 2 * Clock.SECOND;

        } else {
            return 3 * Clock.SECOND;
        }


    }

    public static void main(String[] args) throws Exception {
        ClockWorker worker = ClockWorkerContext.getWorker();


        WorkSchedule schedule = worker.createSchedule(WaterLevelIndicator.class).setStrictMode(true);
        schedule.activate();


        worker.shutdown(20 * Clock.SECOND);
    }
}
