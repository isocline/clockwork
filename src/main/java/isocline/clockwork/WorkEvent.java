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

/**
 *
 *  A interface that represents the event to be delivered when the work object is executed.
 *
 * @see Work
 * @see FlowableWork
 * @author Richard D. Kim
 */
public interface WorkEvent {


    /**
     * Returns the event name.
     *
     * @return
     */
    public String getEventName();


    /**
     *
     * Set the WorkSchedule object.
     *
     * @param sechedule
     */
    void setWorkSechedule(WorkSchedule sechedule);


    /**
     * Returns the WorkSchedule object.
     *
     * @return
     */
    WorkSchedule getWorkSchedule();


    /**
     *
     * Set additional custom attribute values.
     *
     *
     * @param key
     * @param value
     */
    void setAttribute(String key, Object value);

    /**
     * @param key
     * @return
     */
    Object getAttribute(String key);

    /**
     * Delete the attribute value.
     *
     * @param key
     * @return
     */
    Object removeAttribute(String key);


    /**
     * Copy internal information to another Event object.
     *
     * @param event
     */
    void copyTo(WorkEvent event);


    /**
     * Creates a child object of the object.
     * The child object shares the property value information of the parent object.
     *
     * @param eventName
     * @return
     */
    WorkEvent createChild(String eventName);


    WorkEvent root();


    /**
     * Set error information.
     *
     * @param e
     */
    void setThrowable(Throwable e) ;


    /**
     * Returns error information. Null is returned in normal state.
     *
     */
    Throwable getThrowable();


    /**
     * Defines the valid time at which the event occurs.
     *
     * @param time
     */
    void setFireTime(long time);


    /**
     *
     *  Returns the valid time at which the event occurs.
     *
     * @return
     */
    long getFireTime();


}
