package isocline.clockwork.object;

import isocline.clockwork.WorkProcessorFactory;
import isocline.clockwork.EventInfo;
import isocline.clockwork.Work;

public abstract class LiveObject implements Work {


    public LiveObject() {


    }


    public void start() {

        WorkProcessorFactory.getDefaultProcessor().createSchedule(this).activate();
    }

    public abstract long check();


    @Override
    public long execute(EventInfo event) throws InterruptedException {

        return check();
    }
}
