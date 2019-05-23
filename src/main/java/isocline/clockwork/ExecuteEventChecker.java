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
 * Provides the function to check whether method is executed every time execution event occurs.
 * It is used when the user wants to check whether it is executed according to the situation.
 *
 * @see WorkSchedule
 *
 */
public interface ExecuteEventChecker {


    /**
     * And judges whether or not it is executed.
     *
     * @param time the number of milliseconds since January 1, 1970, 00:00:00 GMT for the date and time specified by the arguments.
     * @return true/false
     */
    boolean check(long time);
}
