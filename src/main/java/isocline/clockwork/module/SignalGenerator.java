package isocline.clockwork.module;

import isocline.clockwork.WorkEvent;
import isocline.clockwork.Work;

public class SignalGenerator implements Work {


    private long timeGap = 1000;

    private String eventName = "clockwork:signal";


    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    @Override
    public long execute(WorkEvent event) throws InterruptedException {



        WorkEvent newEvent = new WorkEvent(eventName);
        event.copyTo(newEvent);


        event.getWorkSchedule().getWorkProcessor().raiseEvent(eventName, newEvent);

        //event.getWorkSchedule().getWorkProcessor().raiseEvent(eventName, event);
        //WorkProcessorFactory.getDefaultProcessor().raiseEvent(eventName, newEvent);

        System.out.println("FIRE event:"+eventName);

        return timeGap;
    }
}
