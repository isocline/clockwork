package isocline.clockwork.examples.ext;

import isocline.clockwork.*;
import org.apache.log4j.Logger;


/**
 * This program is an indicator program which checks the water level periodically.
 * The water level grows up then Program check more often,
 * but it was a level down then check sometimes.
 */
public class MultiPlexer implements Work {

    private static Logger logger = Logger.getLogger(MultiPlexer.class.getName());

    private int seq;

    private String id;



    MultiPlexer(String id,int seq) {
        this.id = id;
        this.seq=seq;


    }

    @Override
    public long execute(EventInfo event) throws InterruptedException {
        //System.out.println("== "+System.currentTimeMillis());

        logger.debug(id+" "+seq +" send ");



        /*
        for(int i=0;i<10;i++) {
            logger.info("execute(EventInfo event) throws InterruptedException");
        }
        */

        /*
        int s = 0;
        for(int i=0;i<10000;i++) {
            if(i%20==0) {
                Thread.sleep(0, 10);
            }
            s=s+  (int) (100*Math.random());
        }
        */
        //logger.debug(id+" >> "+seq +" END "+s);


        return 100;
    }

    @Override
    public String toString() {
        return "MultiPlexer{" +
                "seq=" + seq +
                ", id='" + id + '\'' +
                '}';
    }

    public static void main(String[] args) throws Exception {
        ClockWorker worker = new ClockWorker("perform", Configuration.PERFORMANCE);

        long startTime = Clock.nextSecond(900);

        System.out.println(startTime);
        System.out.println(System.currentTimeMillis());



        for(int i=0;i< 10;i++ ) {
            WorkSchedule schedule = worker.createSchedule(new MultiPlexer("A",i)).setStartTime(startTime+i*10);
            schedule.activate();
        }



        /*

        for(int i=0;i<10;i++ ) {
            WorkSchedule schedule = worker.createSchedule(new MultiPlexer("B",i)).setStrictMode(true).setStartTime(startTime+i*100+50);
            schedule.activate();
        }
        */







        worker.shutdown(600 * Clock.SECOND);
    }

}
