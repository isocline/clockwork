package isocline.clockwork.event;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EventRepository<K,V> extends HashMap<K,V> {

    private Map<String, EventSet> eventMap = new ConcurrentHashMap<>();


    public String[] setBindEventNames(String eventNameMeta) {

        EventSet eventSet = new EventSet(eventNameMeta);

        String[] eventNames = eventNameMeta.split("&");

        if(eventNames.length>1) {
            for (String eventName : eventNames) {
                eventSet.add(eventName);
            }
            for (String eventName : eventNames) {
                eventMap.put(eventName, eventSet);
            }
        }

        return eventNames;
    }

    public EventSet getEventSet(String eventName) {
        EventSet eventSet = eventMap.get(eventName);

        return eventSet;
    }
}
