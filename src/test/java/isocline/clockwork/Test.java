package isocline.clockwork;

public class Test {

    public static void main(String[] args) throws Exception {


        ClockWorker worker = ClockWorkerContext.getWorker();


        for (int i = 0; i < 1; i++) {

            TestJob work = new TestJob(i);
            WorkSchedule schedule = worker.createSchedule(work).bindEvent("fire").setStartDelay(1000);

            schedule.start();

        }

        for(int i=0;i<30;i++) {

            System.out.println(worker.getWorkQueueSize() +"  start");
            System.out.println(worker.getRunningWorkCount() +"  chk");

            if(i==3) {
                EventInfo event = new EventInfo();
                event.put("x","X value setup");
                worker.raiseEvent("fire", event );
                System.err.println("xxxxxxx");
            }

            Thread.sleep(1000);
        }


        worker.shutdown();
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
            System.out.println(new java.util.Date() + " " + msg);

        }
    }


}
