package isocline.clockwork;

public class Test {

    public static void main(String[] args) throws Exception {


        ClockWorker worker = ClockWorkerContext.getWorker();


        for (int i = 0; i < 10; i++) {
            TestJob work = new TestJob(i);
            WorkSchedule schedule = new WorkSchedule(work);

            worker.addWorkSchedule(schedule.setStartDelay(1000));


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

        public long execute() throws InterruptedException {

            count ++;

            log(seq + "th job execute. count="+count);


            if (count % 3 == 0) {
                return 2 * Clock.SECOND;
            } else if (count >3) {
                return Clock.FINISH;
            } else {
                return Clock.SECOND;
            }


        }

        private void log(String msg) {
            System.out.println(new java.util.Date() + " " + msg);

        }
    }


}
