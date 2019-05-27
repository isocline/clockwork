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
 * A skeletal {@link WorkEvent} implementation.
 *
 */
public class WorkEventImpl implements WorkEvent {


    private String eventName = null;

    private long fireTime = -1;


    private Map attributeMap = new Hashtable();

    private WorkSchedule schedule;

    private WorkEvent rootWorkEvent;

    private Throwable throwable;




    WorkEventImpl() {
        this.rootWorkEvent = this;
    }

    /**
     *
     * @param eventName
     */
    WorkEventImpl(String eventName) {
        this.eventName = eventName;
        this.rootWorkEvent = this;
    }

    /**
     *
     * @param eventName
     * @param rootWorkEvent
     */
    WorkEventImpl(String eventName, WorkEvent rootWorkEvent) {
        this.eventName = eventName;
        this.rootWorkEvent = rootWorkEvent;
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


    /**
     * Creates a new {@link WorkEvent} that has parent property information.
     *
     * @param eventName the name of the event to create; may not be empty
     * @return the newly created WorkEvent
     */
    public WorkEvent createChild(String eventName) {

        if(eventName==null || eventName.trim().length()==0) {
            throw new IllegalArgumentException("name is empty");
        }


        WorkEventImpl newEvent = new WorkEventImpl(eventName,this.rootWorkEvent);
        //newEvent.attributeMap = this.attributeMap;
        newEvent.schedule = this.schedule;


        return newEvent;


    }


    @Override
    public void setFireTime(long time) {
        this.fireTime = time;
    }

    @Override
    public long getFireTime() {
        return this.fireTime;
    }

    @Override
    public void setThrowable(Throwable e) {
        this.throwable = e;

    }

    @Override
    public Throwable getThrowable() {
        return this.throwable;
    }

    @Override
    public WorkEvent root() {
        return this.rootWorkEvent;
    }
}
