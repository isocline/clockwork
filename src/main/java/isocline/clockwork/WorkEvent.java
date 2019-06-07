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

import java.util.stream.Stream;

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
     * @return name of event
     */
    public String getEventName();



    int count();

    /**
     *
     * Set the Plan object.
     *
     * @param plan instance of Plan
     */
    void setPlan(Plan plan);


    /**
     * Returns the Plan object.
     *
     * @return instance of Plan
     */
    Plan getPlan();


    /**
     *
     * Set additional custom attribute values.
     *
     *
     * @param key the key name to which the object is bound; cannot be null
     * @param value the object to be bound
     */
    void setAttribute(String key, Object value);

    /**
     * Returns the object bound with the specified name in this session,
     * or null if no object is bound under the name.
     *
     * @param key  a string specifying the name of the object
     * @return the object with the specified name
     */
    Object getAttribute(String key);

    /**
     * Delete the attribute value.
     *
     * @param key the name of the object to remove from this session
     * @return the name of the object to remove from this session
     */
    Object removeAttribute(String key);


    /**
     * Copy internal information to another Event object.
     *
     * @param event an instance of WorkEvent that want to copy
     */
    void copyTo(WorkEvent event);


    /**
     * Creates a child object of the object.
     * The child object shares the property value information of the parent object.
     *
     * @param eventName name of event
     * @return an new instance of WorkEvent
     */
    WorkEvent createChild(String eventName);


    /**
     * Returns the earliest event among the events associated with the current event.
     *
     * @return first origin of WorkEvent
     */
    WorkEvent origin();


    /**
     * Set error information.
     *
     * @param e Throwable error
     */
    void setThrowable(Throwable e) ;


    /**
     * Returns error information. Null is returned in normal state.
     *
     *@return an instance of Throwable
     */
    Throwable getThrowable();


    /**
     * Defines the valid time at which the event occurs.
     *
     * @param time the milliseconds since January 1, 1970, 00:00:00 GMT.
     */
    void setFireTime(long time);


    /**
     *
     *  Returns the valid time at which the event occurs.
     *
     * @return time the milliseconds since January 1, 1970, 00:00:00 GMT.
     */
    long getFireTime();


    String getFireEventName();


    void setFireEventName(String eventName);



    Stream getStream();

    Object getResult();



}
