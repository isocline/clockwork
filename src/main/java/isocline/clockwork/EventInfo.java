package isocline.clockwork;

import java.util.HashMap;

public class EventInfo extends HashMap {



    private String eventName;

    private WorkSchedule schedule;

    public EventInfo() {

    }

    public EventInfo(String eventName) {
        this.eventName = eventName;
    }

    public String getEventName() {
        return this.eventName;
    }

    void setWorkSechedule(WorkSchedule sechedule) {
        this.schedule = sechedule;
    }

    public WorkSchedule getWorkSchedule() {

        return this.schedule;
    }



}
