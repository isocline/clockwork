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
package isocline.clockwork;

import java.util.Hashtable;
import java.util.Map;


/**
 *
 *
 */
public class EventInfo {


    private String eventName;


    private Map attributeMap = new Hashtable();


    private WorkSchedule schedule;


    /**
     *
     */
    public EventInfo() {

    }

    public EventInfo(String eventName) {
        this.eventName = eventName;
    }

    public EventInfo setEventName(String eventName) {
        this.eventName = eventName;
        return this;
    }

    public String getEventName() {
        return this.eventName;
    }

    void setWorkSechedule(WorkSchedule sechedule) {
        this.schedule = sechedule;
    }


    /**
     *
     * @return
     */
    public WorkSchedule getWorkSchedule() {

        return this.schedule;
    }


    /**
     *
     * @param key
     * @param value
     */
    public void setAttribute(String key, Object value) {
        this.attributeMap.put(key, value);

    }

    /**
     *
     * @param key
     * @return
     */
    public Object getAttribute(String key) {
        return this.attributeMap.get(key);
    }


    /**
     *
     * @param key
     * @return
     */
    public Object removeAttribute(String key) {
        return this.attributeMap.remove(key);
    }


    /**
     *
     * @param event
     */
    public void copyTo(EventInfo event) {
        event.schedule = this.schedule;
        event.attributeMap = this.attributeMap;
    }


}
