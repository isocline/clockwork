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
 *
 */
public interface WorkEvent {


    public String getEventName();


    void setWorkSechedule(WorkSchedule sechedule);


    /**
     * @return
     */
    WorkSchedule getWorkSchedule();


    /**
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
     * @param key
     * @return
     */
    Object removeAttribute(String key);


    /**
     * @param event
     */
    void copyTo(WorkEvent event);


    /**
     *
     * @param eventName
     * @return
     */
    WorkEvent create(String eventName);



    void setFireTime(long time);

    long getFireTime();


}
