package isocline.clockwork;

public class Test {

    public static void main(String[] args) throws Exception {


        ClockWorker worker = ClockWorkerContext.getWorker();


        for (int i = 0; i < 1; i++) {

            TestJob work = new TestJob(i);
            WorkSchedule schedule = worker.createSchedule(work).bindEvent("fire").setStartDelay(1000).setEndDateTime("2019-01-15T10:48:30+09:00");

            schedule.start();

        }

        for(int i=0;i<30;i++) {

            System.out.println(worker.getWorkQueueSize() +"  start");
            System.out.println(worker.getRunningWorkCount() +"  chk");

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

            log(seq + "th job execute. count="+count);



            if (count <= 3) {
                return Clock.SECOND;
            } else if (count >3 && count<100) {
                return 2*Clock.SECOND;
            } else {
                return Clock.FINISH;
            }


        }

        private void log(String msg) {
            System.out.println(new java.util.Date() + " " + msg);

        }
    }


}
