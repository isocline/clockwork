package isocline.clockwork;

import java.util.Date;

public class ScheduleConfig {

    private long waitTime = Long.MIN_VALUE;

    private long intervalTime = Long.MIN_VALUE;

    private Date startDateTime = null;

    private Date endDateTime = null;

    private long finishTime = Long.MIN_VALUE;

    private String[] eventNames= null;

    private WorkSession workSession;

    private boolean isSecondBaseMode;

    private boolean isSecondBaseModeUsed = false;

    private long jitter = Long.MIN_VALUE;

    private boolean activeeMode = true;


    public ScheduleConfig setStartDelay(long waitTime) {
        this.waitTime = waitTime;

        return this;

    }


    public ScheduleConfig setRepeatInterval(long intervalTime) {
        this.intervalTime = intervalTime;

        return this;

    }


    /**
     * ISO 8601 Data elements and interchange formats (https://en.wikipedia.org/wiki/ISO_8601)
     *
     * @param isoDateTime
     */
    public ScheduleConfig setStartDateTime(String isoDateTime) throws java.text.ParseException {

        this.startDateTime = Clock.getDate(isoDateTime);

        return this;
    }


    public ScheduleConfig setStartDateTime(Date startDateTime) {

        this.startDateTime = startDateTime;
        return this;
    }


    /**
     * ISO 8601 Data elements and interchange formats (https://en.wikipedia.org/wiki/ISO_8601)
     *
     * @param isoDateTime
     */
    public ScheduleConfig setEndDateTime(String isoDateTime) throws java.text.ParseException {

        return setEndDateTime(Clock.getDate(isoDateTime));
    }

    public ScheduleConfig setEndDateTime(Date endDateTime) {

        this.endDateTime = endDateTime;
        return this;
    }

    public ScheduleConfig setFinishTime(long milliSeconds) {
        this.finishTime = milliSeconds;
        return this;
    }


    public ScheduleConfig setWorkSession(String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        this.workSession = (WorkSession) Class.forName(className).newInstance();
        return this;
    }


    public ScheduleConfig setWorkSession(WorkSession workSession) throws ClassNotFoundException {

        this.workSession = workSession;
        return this;
    }


    public ScheduleConfig bindEvent(String... eventNames) {
        int size = eventNames.length;
        this.eventNames = new String[size];

        int count = 0;
        for (String eventName : eventNames) {
            this.eventNames[count] = eventName;
            count++;
        }

        return this;
    }


    public ScheduleConfig setSecondBaseMode(boolean isSecondBaseMode) {
        isSecondBaseModeUsed = true;
        this.isSecondBaseMode = isSecondBaseMode;
        return this;

    }

    public ScheduleConfig setJitter(long jitter) {
        this.jitter = jitter;

        return this;
    }

    public void setActiveeMode(boolean activeeMode) {
        this.activeeMode = activeeMode;
    }


    void settup(WorkSchedule workSchedule) {

        if(waitTime != Long.MIN_VALUE) {
            workSchedule.setStartDelay(waitTime);
        }

        if(intervalTime!=Long.MIN_VALUE) {
            workSchedule.setRepeatInterval(intervalTime);
        }

        if(this.startDateTime!=null) {
            workSchedule.setStartDateTime(startDateTime);
        }

        if(endDateTime!=null) {
            workSchedule.setEndDateTime(endDateTime);
        }

        if(finishTime!=Long.MIN_VALUE) {
            workSchedule.setFinishTime(finishTime);
        }

        if(eventNames!=null) {
            workSchedule.bindEvent(eventNames);
        }

        if(workSession!=null) {
            workSchedule.setWorkSession(workSession);
        }

        if(isSecondBaseModeUsed) {
            workSchedule.setSecondBaseMode(isSecondBaseMode);
        }

        if(jitter!=Long.MIN_VALUE) {
            workSchedule.setJitter(jitter);
        }
        if(activeeMode) {
            workSchedule.activate();
        }


    }
}
