package isocline.clockwork.object;

import isocline.clockwork.ClockWorkerContext;
import isocline.clockwork.EventInfo;
import isocline.clockwork.Work;

public abstract class LiveObject implements Work {


    public LiveObject() {


    }


    public void start() {

        ClockWorkerContext.getWorker().createSchedule(this).activate();
    }

    public abstract long check();


    @Override
    public long execute(EventInfo event) throws InterruptedException {

        return check();
    }
}
