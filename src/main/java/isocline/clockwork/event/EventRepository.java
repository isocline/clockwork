/*
 * Copyright 2018 The Isocline Project
 *
 * The Isocline Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package isocline.clockwork.event;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 *
 * @param <K>
 * @param <V>
 */
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
