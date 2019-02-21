package isocline.clockwork.object;

import isocline.clockwork.ClockWorkerContext;
import isocline.clockwork.EventInfo;
import isocline.clockwork.Work;

import java.util.function.Consumer;

public class AsyncObject implements Work {

    private boolean isFinish = false;


    public AsyncObject() {


    }


    public void start() {

        ClockWorkerContext.getWorker().createSchedule(this).activate();


    }


    public synchronized  void waitUntilFinish(long timeout) {
        if(!isFinish) {
            try {
                wait(timeout);
            }catch (InterruptedException ie) {

            }
        }
    }

    @WorkInfo(start = true, next = "step2,step3")
    public void step1() {

    }

    @WorkInfo(id="step2", async = true)
    public void asyncStep1() {

    }

    @WorkInfo(id="step3",  async = true)
    public void asyncStep2() {

    }

    @WorkInfo(startAfter = "step2,step3")
    public void stepEnd1() {

    }

    protected void finishJob() {

    }


    public void step(Consumer action) {


    }

    protected int processFlow(int step, EventInfo event)  {

        step(this::step1);






        switch (step) {
            case 0:
                step1();
                return 1;

            case 1:
                asyncStep1();
                return 2;
            case 2:
                asyncStep2();
                break;

            case 3:
                stepEnd1();

            default:
                finishJob();

        }



        return 1;
    }

    private void step1(Object o) {
    }


    @Override
    public long execute(EventInfo event) throws InterruptedException {


        return 1;
    }
}
