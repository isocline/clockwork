package isocline.clockwork.module;

import isocline.clockwork.Work;
import isocline.clockwork.WorkEvent;

public class WorkEventGenerator implements Work {


    private long timeGap = 1000;

    private String eventName = "clockwork:signal";


    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public void setRepeatTime(long repeatTime) {
        this.timeGap = repeatTime;
    }

    @Override
    public long execute(WorkEvent event) throws InterruptedException {


        WorkEvent newEvent = event.create(eventName);


        event.getWorkSchedule().getWorkProcessor().raiseEvent(eventName, newEvent);

        //event.getWorkSchedule().getWorkProcessor().raiseEvent(eventName, event);
        //WorkProcessorFactory.getDefaultProcessor().raiseEvent(eventName, newEvent);

        System.out.println("fire event [" + eventName+"]");

        return timeGap;
    }
}
