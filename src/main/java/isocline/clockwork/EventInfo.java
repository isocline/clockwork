package isocline.clockwork;

import java.util.HashMap;

public class EventInfo extends HashMap {


    private WorkSchedule schedule;

    void setWorkSechedule(WorkSchedule sechedule) {
        this.schedule = sechedule;
    }

    public WorkSchedule getWorkSchedule() {

        return this.schedule;
    }



}
