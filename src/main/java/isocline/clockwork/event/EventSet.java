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

import java.util.HashSet;

/**
 *
 *
 */
public class EventSet extends HashSet<String> {

    private String eventSetName;

    /**
     * EventSet is bundle of event names.
     * ex)  event1&event2
     *
     * @param eventSetName
     */
    public EventSet(String eventSetName) {
        this.eventSetName = eventSetName;
    }


    /**
     * Returns a EventSetName
     * @return
     */
    public String getEventSetName() {
        return this.eventSetName;
    }


    /**
     * Check
     *
     * @param eventName
     * @return
     */
    public boolean isRaiseEventReady(String eventName) {
        this.remove(eventName);
        if(this.size()==0) {
            return true;
        }

        return false;
    }
}