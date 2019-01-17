package isocline.clockwork;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Test {

    public static void main(String[] args) throws Exception {


        ClockWorker worker = ClockWorkerContext.getWorker();


        for (int i = 0; i < 1; i++) {

            TestJob work = new TestJob(i);
            //WorkSchedule schedule = worker.createSchedule(work).bindEvent("fire").setStartDelay(1000);
            //WorkSchedule schedule = worker.createSchedule(work).bindEvent("fire").setStartDelay(Clock.at("2019-01-17T13:32:30+09:00"));
            WorkSchedule schedule = worker.createSchedule(work).bindEvent("fire").setSecondBaseMode(true);

            schedule.start();

        }

        /*
        for(int i=0;i<30;i++) {

            if(worker.getManagedWorkCount()==0) {
                break;
            }

            System.out.println("WORKER SIZE = "+ worker.getWorkQueueSize() +"  start");
            System.out.println("WORK COUNT = "+worker.getManagedWorkCount() +"  chk");

            if(i==15) {
                EventInfo event = new EventInfo();
                event.put("x","X value setup");
                worker.raiseEvent("fire", event );
                System.err.println("xxxxxxx");
            }

            Thread.sleep(3000);
        }
        */


        worker.shutdown(10000);
    }


    public static class TestJob implements Work {


        private int seq;
        private int count = 0;

        public TestJob(int seq) {
            this.seq = seq;
        }

        public long execute(EventInfo event) throws InterruptedException {

            count ++;

            Object eventMsg = event.get("x");
            log(seq + "th job execute. count="+count + " "+eventMsg);

            if(eventMsg!=null) {
                return Clock.FINISH;
            }



            if (count <= 3) {
                return Clock.SECOND;
            } else if (count >3 && count<6) {
                return 2*Clock.SECOND;
            } else if(count >= 6) {
                return Clock.SLEEP;
            }else {
                return Clock.FINISH;
            }


        }

        private void log(String msg) {

            final String input = "20120823151034.567";
            final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

            Date d = new Date();


            System.out.println(df.format(d) + " " + msg);

        }
    }


}
