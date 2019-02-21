package isocline.clockwork.event;

import java.util.HashSet;

public class EventSet extends HashSet<String> {

    private String eventSetName;

    public EventSet(String eventSetName) {
        this.eventSetName = eventSetName;
    }

    public String getEventSetName() {
        return this.eventSetName;
    }


    /*
    eventSet.remove(eventName);

        if(eventSet.size()==0) {
     */

    public boolean isRaiseEventReady(String eventName) {
        this.remove(eventName);
        if(this.size()==0) {
            return true;
        }

        return false;
    }
}