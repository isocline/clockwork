package isocline.clockwork;

import java.util.Hashtable;
import java.util.Map;

public class EventInfo {


    private Map attributeMap = new Hashtable();

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


    public void setAttribute(String key, Object value) {
        this.attributeMap.put(key, value);

    }

    public Object getAttribute(String key) {
        return this.attributeMap.get(key);
    }

    public Object removeAttribute(String key) {
        return this.attributeMap.remove(key);
    }

    public void copyTo(EventInfo event) {
        event.schedule = this.schedule;
        event.attributeMap = this.attributeMap;
    }



}
