package isocline.clockwork;

public class Test2 {

    public static void main(String[] args) throws Exception {


        ClockWorker worker = ClockWorkerContext.getWorker();




        /*
        Scheduler scheduler = new Scheduler(work2);
        scheduler.startAt("20170101 210323");
        scheduler.endAt("20170101 210321");

        scheduler .nextSchedule
        */





        for (int i = 0; i < 10; i++) {
            TestJob work = new TestJob(i);
            //worker.addWork(work, i * 100);


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
