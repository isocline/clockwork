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
 * An interface implemented by an object that defines the underlying operation.
 * The corresponding operation is performed through the activate method call,
 * and the identical operation is performed by the WorkProcessor.
 *
 * @author Richard D. Kim
 */
public interface Work {


    /**
     * This means that the job is waiting. In general, the corresponding status value is used.
     */
    long WAIT = -1;


    /**
     * Which means that the job is terminated without further processing.
     */
    long TERMINATE = -2;


    /**
     * It means a state that repeats immediately without delay.
     * Check the specific country so that it does not become an infinite repeating state.
     */
    long LOOP = 0;






    /**
     *
     * The default response value is the time interval at which this method is called again.
     * The response time unit is milliseconds, and its own method iteration time can be determined through coding.
     * Or if the loop time is set through an external event or API, the WAIT value is returned.
     *
     * @param event WorkEvent
     * @return DelayTime
     * @throws InterruptedException - If interrupt occurs
     */
    long execute(WorkEvent event) throws InterruptedException;


}
