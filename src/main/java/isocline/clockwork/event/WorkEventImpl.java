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

import isocline.clockwork.WorkEvent;
import isocline.clockwork.WorkSchedule;

import java.util.Hashtable;
import java.util.Map;


/**
 *
 *
 */
public class WorkEventImpl implements WorkEvent {


    private String eventName;


    protected Map attributeMap = new Hashtable();


    protected WorkSchedule schedule;


    /**
     *
     */
    WorkEventImpl() {

    }

    WorkEventImpl(String eventName) {
        this.eventName = eventName;
    }

    public WorkEventImpl setEventName(String eventName) {
        this.eventName = eventName;
        return this;
    }

    public String getEventName() {
        return this.eventName;
    }


    public void setWorkSechedule(WorkSchedule sechedule) {
        this.schedule = sechedule;
    }


    /**
     * @return
     */
    public WorkSchedule getWorkSchedule() {

        return this.schedule;
    }


    /**
     * @param key
     * @param value
     */
    public void setAttribute(String key, Object value) {
        this.attributeMap.put(key, value);

    }

    /**
     * @param key
     * @return
     */
    public Object getAttribute(String key) {
        return this.attributeMap.get(key);
    }


    /**
     * @param key
     * @return
     */
    public Object removeAttribute(String key) {
        return this.attributeMap.remove(key);
    }


    /**
     * @param event
     */
    public void copyTo(WorkEvent event) {
        WorkEventImpl event2 = (WorkEventImpl) event;
        event2.schedule = this.schedule;
        event2.attributeMap = this.attributeMap;
    }

    public WorkEventImpl create(String eventName) {

        WorkEventImpl newEvent = new WorkEventImpl(eventName);
        newEvent.attributeMap = this.attributeMap;
        newEvent.schedule = this.schedule;

        return newEvent;


    }


}
