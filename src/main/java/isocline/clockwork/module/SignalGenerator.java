package isocline.clockwork.module;

import isocline.clockwork.ClockWorkerContext;
import isocline.clockwork.EventInfo;
import isocline.clockwork.Work;

public class SignalGenerator implements Work {


    private long timeGap = 1000;

    private String eventName = "clockwork:signal";


    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    @Override
    public long execute(EventInfo event) throws InterruptedException {



        EventInfo newEvent = new EventInfo(eventName);
        event.copyTo(newEvent);



        //event.getWorkSchedule().getClockWorker().raiseEvent(eventName, event);
        ClockWorkerContext.getWorker().raiseEvent(eventName, newEvent);
        System.out.println("FIRE");

        return timeGap;
    }
}
